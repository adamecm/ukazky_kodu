clc
clear all 
close all
%% 
r = 2.18*1e-3;
a = 0.44;


S0 = 762;
I0 = 1;
R0 = 0;


%%
sim('sir.slx',20);
t = ans.tout;
St = ans.data.signals.values(:,1);
It = ans.data.signals.values(:,2);
Rt = ans.data.signals.values(:,3);

figure;
plot(t,St,'r','LineWidth',1)
hold on
plot(t,It,'b','LineWidth',1)
hold on
plot(t,Rt,'g','LineWidth',1)
xlabel('t [den]')
ylabel('počet členů kategorie')
title('SIR model')
legend({'S(t)','I(t)','R(t)'})
