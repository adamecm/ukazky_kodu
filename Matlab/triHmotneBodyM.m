clc
clear all
close all

stepSize = 0.01;
%% mechanická soustava
close all
%k1 = k2 = k3 = k = 1
% m1 = m2 = m = 1
k = 1;
m = 1;
b = 0;

%force
F = 0;

%initial conditions
x1_0 = 0;
x2_0 = 1;
x3_0 = 2;
x1d_0 = 0;
x2d_0 = 0;
x3d_0 = 0;

out = sim("triHmotneBody", 30);
t = out.x.time;

x1 = out.x.signals.values(:,1);
x2 = out.x.signals.values(:,2);
x3 = out.x.signals.values(:,3);

figure(2)
title("graf pohybu hmotnych bodu");
hold on
labels = {'x1','x2','x3'};
plot(t,x1,"b");
plot(t,x2,"g");
plot(t,x3,"r");
legend(labels);
hold off
