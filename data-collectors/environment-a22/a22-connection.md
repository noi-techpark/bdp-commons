The A22-VPN-REVERS-TUNNEL CONUNDRUM
=======================================

"Security by obscurity", describes this situation the best. We just need to connect to a mqtt broker of a22 through a proprietary vpn server called "Checkpoint". Sounds easy... but it's not. This is why:

- there is no existing vpn client for linux (why??)
- vpn connection drops after a few minutes
- vpn client has restricted configurations and some can only be done on the server. There is no way to debug the problematic connection on the client side, nor is there a way to try out advanced configurations.
- timeout is set to 8 hours. How can it be made permanently open?

We tried out different approaches and ended up with a wacky solution which has different pitfalls but currently the only possible approach. We still didn't solve all the problems, but we are in a better spot than before.
This was our approach

# VPN Setup
1. install Checkpoint vpn client on your MAC
2. configure connection with certificate and credentials
3. connect to the vpn

# Reverse tunnel from elastic beanstalk to MAC
1. go to the server deployment directory in .ebextensions and create a config file for the reverse tunnel
2. in the config file create a user and setup ssh authentication(https://github.com/noi-techpark/server-deployment/blob/master/prod-tomcat-dc/.ebextensions/a22-vpn.config)
3. deploy to elastic beanstalk
4. open port 22 for the MAC
5. create a script which setups a reverse tunnel and forwards all data of mqtt broker to port 19998. A CRON will reconnect in case of network drop
```
#!/bin/sh
D=`date '+%Y-%m-%d %H:%M:%S'`
COUNT=`ps auxww | grep 'ssh -i hey/id.rsa -N -R 19998'| grep 'jenkinsmac'|grep -v grep| wc -l`
if [ "$COUNT" -eq "0" ]; then
    echo "$D: tunnel down, restarting..."
    ssh -i hey/id.rsa -N -R 19998:192.168.45.35:61616 jenkinsmac@52.211.137.112 -o ExitOnForwardFailure=True &
    if [ $? -ne 0 ]; then
        echo "$D: restarting the tunnel failed!"
    else
        echo "$D: tunnel up."
    fi
else
    echo "Tunnel is already up and running"
fi
```
6. Deploy the environment-a22 datacollector to production and set the mqtt endpoint to localhost:19998 as configured in the ssh tunnel.
