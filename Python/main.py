""" created by Martin Adamec (team Black) for KKY/ITE purposes, 2021/2022 """

import machine
import onewire
import ds18x20
import time
import network
import ntptime
import json
import umqtt.robust as mqtt
import sys
import urequests

""" loading configuration"""
try:
  with open("config.json","r") as f:
    CONFIG = json.load(f)
except:
  print("Config file seems to be corrupted, make sure it is in proper JSON format.")
  sys.exit()

PUBLISHING_PERIOD = CONFIG["int_publishing_period_nanoseconds"]
CONNECTION_TIME_LIMIT = CONFIG["int_connection_time_limit_seconds"]

WIFI_NAME = CONFIG["string_WiFi_network_name"]
WIFI_PSWD = CONFIG["string_WiFi_network_password"]

BROKER_IP = CONFIG["string_MQTT_broker_IP"]
BROKER_PORT = CONFIG["int_MQTT_broker_PORT"]
BROKER_UNAME = CONFIG["string_MQTT_broker_username"]
BROKER_PASSWD = CONFIG["string_MQTT_broker_password"]
TOPIC = CONFIG["string_MQTT_broker_publishing_topic"]

SOS_URL = "https://sulis17.zcu.cz:443/sos"

measurements_list = []
something_stored = False
wifi_down = False

def wifi_connect(time_limited = False):     
    """ 
    Function loops for CONNECTION_TIME_LIMIT seconds until it either connects to the provided wifi network
    or gives up if time_limited = True
    """
    sta_if = network.WLAN(network.STA_IF)
    if not sta_if.isconnected():
        sta_if.active(True)
        sta_if.connect(WIFI_NAME, WIFI_PSWD)
        connection_attemp_start = time.time()
        while not sta_if.isconnected():
          if time_limited and ((time.time()-connection_attemp_start)>=CONNECTION_TIME_LIMIT):
            print("Attempt to connect to wifi was unsuccessful")
            break
          pass
    return sta_if

def u_zfill(s, width):
  """ Replication of python built-in function "zfill()" suited for Micropython limitations """
  return "{:0>{w}}".format(str(s), w=width)

def create_timestamp(rtc):
  """
  Function creates a timestamp string in UTC time
  Format of the timestamp string: "YYYY-MM-DD"T"Hours:Minutes:Seconds.Subseconds+TIMEZONE_OFFSET"
  Example of a correct timestamp string: "2004-07-08T06:08:03.059+03:00"
  """

  timestamp = rtc.datetime()
  timestamp = list(timestamp)
  timestampString = str(timestamp[0])+"-"+u_zfill(timestamp[1],2)+"-"+u_zfill(timestamp[2],2)+"T"+u_zfill(timestamp[4],2)+":"+u_zfill(timestamp[5],2)+":"+u_zfill(timestamp[6],2)+"."+u_zfill(timestamp[7],6)
  return timestampString

def set_current_time():  
  """ Function synchronizes the internal RTC of the board using the NTP """
  rtc = machine.RTC()
  while True:
    try:
      ntptime.host = "0.europe.pool.ntp.org"
      ntptime.settime()
      break
    except:
      print("NTP sync not successful, retrying...")
      continue  
  return rtc

def send_to_broker(client, topic, payload, qos=0, retain=False):    
    """ Function sends measured data to MQTT broker """
    client.publish(topic, payload, retain=retain, qos=qos)


def initialize_client(client_id="black", server=BROKER_IP, port=BROKER_PORT, user=BROKER_UNAME, password=BROKER_PASSWD, keepalive=60):
    """ Function creates an instance of an MQTT client for publishing to the chosen broker """
    client = mqtt.MQTTClient(client_id=client_id,server=server,port=port,user=user,password=password,keepalive=keepalive)
    return client

def no_sensor_SOS():
  """ Function sends HTTP SOS message to GUI """
  errorMsg = json.dumps({"espsos": "No sensor connected, please reconnect manually."})
  x = urequests.post(SOS_URL, data = errorMsg)

def no_client_SOS():
  """ Function sends HTTP SOS message to GUI """
  errorMsg = json.dumps({"espsos": "Could not connect to MQTT broker, please check its status."})
  x = urequests.post(SOS_URL, data = errorMsg)

def scan_pin_for_sensor(ds_sensor,client):
  """ Function scans pin for temperature sensor, sends alert if no sensor is found """
  for try_number in range(1,6):
    roms = ds_sensor.scan()
    if(len(roms)) == 1:
      return roms[0]
    print("No sensor found, checking again (attempt",try_number, " out of 5)")
    time.sleep(5)
  else:    
    try:
        no_sensor_SOS()
        print("No sensor connected, check manually and restart the device")
    except:
        print("ESP OUT")
    sys.exit()

""" MAIN """
#######################################################################################################################################

#ESP setup routines
wifi = wifi_connect()
rtc = set_current_time()
client = initialize_client()


ds_pin = machine.Pin(12)
ds_sensor = ds18x20.DS18X20(onewire.OneWire(ds_pin))
rom = scan_pin_for_sensor(ds_sensor,client)

time.sleep(40) #some time for the temperature sensor to get ready, otherwise the first temperature will always be 25 degrees

orig_time_sec = rtc.datetime()[6]
orig_time_subsec = rtc.datetime()[7]
orig_time_hours = rtc.datetime()[4]

""" infinite main loop """
while True:
  if abs(rtc.datetime()[4]-orig_time_hours) >= 1: #if an hour has passed -> resync RTC
    print("resync")
    rtc = set_current_time()
    orig_time_hours = rtc.datetime()[4]

  try:
    ds_sensor.convert_temp()
  except:
    print("Missing sensor")
    try:
      rom = scan_pin_for_sensor(ds_sensor,client)
    except:
      print("ESP OUT")
      sys.exit()

  data_log = {"team_name": "black", "created_on": create_timestamp(rtc), "temperature": float("{:.2f}".format(ds_sensor.read_temp(rom)))}
  json_datalog = json.dumps(data_log)

  reduce_sleep_by = 0
  try:
    client.connect()
    wifi_down = False
  except:
    print("Broker unavailable")
    try:
      no_client_SOS()
    except:
      print("Wifi is down")
      wifi_down = True

  if not wifi_down:  
    if something_stored:
      print("Sending storage")
      for msg in measurements_list:
        send_to_broker(client, TOPIC, msg,qos=1)
        time.sleep_ms(10)
      measurements_list = []
      something_stored = False
    else:
      print(json_datalog)
      send_to_broker(client, TOPIC, json_datalog,qos=1)
    
    client.disconnect()
  else:
    measurements_list.append(json_datalog)
    something_stored = True
    wifi = wifi_connect(time_limited=True)
    reduce_sleep_by = CONNECTION_TIME_LIMIT

  time.sleep(50-CONNECTION_TIME_LIMIT)

  while not ((rtc.datetime()[6] == orig_time_sec) and (rtc.datetime()[7] == orig_time_subsec)): #rtc.datetime check for high-precission 1 minute intervals
    time.sleep_us(100)

