# Disclaimer

This tool and code is only for demo purposes as a proof-of-concept. There is only minor error protection and this should not be used in production.

# HIPWedge

HIPWedge is a tool for Hon3w3ll Android devices with built-in barcode scanner. The tool will capture all scanned barcodes and transmit to a TCP/IP server (see SocketWedge), for example, on a Windows PC. The socket server can then translate the received barcode data into key strokes and insert data into text fields/input.

![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/HIPWedge01.png)

## Installation

Install the apk the normal way and then configure the device to use the provided DataEditing plugin: go to Settings > Honeywell Settings > Scanning > Internal Scanner > Default profile > Data Processing Settings > Data Editing Plugin. When the Data Editing Plugin setting is opened, the Plugin part of the tool should be listed as  com.demos.hipwedge/.DataEditRecv. Select this plugin and it will show below Data Editing Plugin above Settings.

![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/dataeditingplugin00.png) ![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/dataeditingplugin01.png)

## Settings

![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/dataeditingplugin02.png) ![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/dataeditingplugin03.png)

The settings allow to define the server ip address and port to be used. Default is 192.168.0.40 and 52401.

### Host/IP

Set the host IP.

### Host Port

Define the port to be used.

### Enable/Disable IP Wedge

This enables or diables IP Wedge. If disabled, not data will be send to the server IP.

### Enable local barcode data processing

If enabled, all data can also be processed by applications running on the device. If disabled, the tool will consume the barcode data and does not forward it.

# The main activity

The main activity HIPWedge will show the scanned data and the data received back by the server. The provided SocketWedge echo's all data back to HIPWedge.

![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/HIPWedge02.png)

# SocketWedge

Just to give you an idea of the server application.

![HipWedge](https://github.com/hjgode/HIPWedge/blob/master/doc/socketwedge.png)

see [SocketWedge](https://github.com/hjgode/SocketWedge)
