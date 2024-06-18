#!/bin/bash

# get list - pGuide.getElencoIdentificativiParcheggi
curl -H "Content-Type: text/xml" --data '<?xml version="1.0"?>
<methodCall>
    <methodName>pGuide.getElencoIdentificativiParcheggi</methodName>
    <params> </params>
</methodCall>
' http://109.117.22.203:7075/RPC2


# get single stations metadata - pGuide.getCaratteristicheParcheggio
# param = station id
curl -H "Content-Type: text/xml" --data '<?xml version="1.0"?>
<methodCall>
    <methodName>pGuide.getCaratteristicheParcheggio</methodName>
    <params>
        <param>
            <value>
                <int>106</int>
            </value>
        </param>
    </params>
</methodCall>
' http://109.117.22.203:7075/RPC2

# get single stations metadata - pGuide.getCaratteristicheParcheggio
curl -H "Content-Type: text/xml" --data '<?xml version="1.0"?>
<methodCall>
    <methodName>pGuide.getCaratteristicheParcheggio</methodName>
    <params>
        <param>
            <value>
                <int>107</int>
            </value>
        </param>
    </params>
</methodCall>
' http://109.117.22.203:7075/RPC2

# get single stations data - pGuide.getPostiLiberiParcheggio
# param = station id
curl -H "Content-Type: text/xml" --data '<?xml version="1.0"?>
<methodCall>
    <methodName>pGuide.getPostiLiberiParcheggio</methodName>
    <params>
        <param>
            <value>
                <int>106</int>
            </value>
        </param>
    </params>
</methodCall>
' http://109.117.22.203:7075/RPC2


# get single stations data ext(?) - pGuide.getPostiLiberiParcheggioExt
# param = station id
curl -H "Content-Type: text/xml" --data '<?xml version="1.0"?>
<methodCall>
    <methodName>pGuide.getPostiLiberiParcheggioExt</methodName>
    <params>
        <param>
            <value>
                <int>106</int>
            </value>
        </param>
    </params>
</methodCall>
' http://109.117.22.203:7075/RPC2