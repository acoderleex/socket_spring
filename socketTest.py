#!/usr/bin/env python

from socketIO import SocketIO, LoggingNamespace

socketIO = SocketIO('192.168.3.139', 9081)
socketIO.emit('refreshData','12')
# socketIO.emit('replaceData','12')
socketIO.wait()
