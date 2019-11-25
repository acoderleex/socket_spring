#!/usr/bin/env python

from socketIO_client import SocketIO, LoggingNamespace

socketIO = SocketIO('192.168.3.139', 9081)
socketIO.emit('refreshData','12')
socketIO.wait()
