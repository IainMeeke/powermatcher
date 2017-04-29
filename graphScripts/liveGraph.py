#!/usr/bin/env python

import pylab as plt
import numpy as np
from collections import deque
import csv
import time
import sys
import os



updateTime = 0.01

def readLatestLine(filePath):
	with open(filePath, 'rb') as f:
		try:
			lastrow = deque(csv.reader(f), 1)[0]
		except IndexError:  # empty file
			lastrow = None
		return lastrow

def parseLineToArray(line):
	demandValues = line[len(line) -2]
	demandListStrings = demandValues.split("#")
	demandList = map(float, demandListStrings)
	return demandList

def loggingStarted(filePath):
	lastLine = readLatestLine(filePath)
	if lastLine[0].startswith('log'):
		return False
	else:
		return True

def getAgentId(filePath):
	lastLine = readLatestLine(filePath)
	name = lastLine[2]
	return name

#return 0 if the price log doesn't exist or hasn't started
def getPrice(logLocation):
	priceFileName = logLocation + "bid/price1511.csv"
	if os.path.isfile(priceFileName) and loggingStarted(priceFileName):
		lastLine = readLatestLine(priceFileName)
		price = float(lastLine[7])
		return price
	return 0

def makeBidCurve(logLocation, fileName):
	filePath = logLocation + fileName

	while not loggingStarted(filePath):
		time.sleep(updateTime)

	price = getPrice(logLocation)

	prevLine = readLatestLine(filePath)
	demandList = parseLineToArray(prevLine)
	X = np.linspace(0,1,len(demandList))
	Y = demandList
	plt.xlabel('Price (' + unichr(8364) + ')')
	plt.ylabel('Demand (W)')
	plt.title('Bid Curve for '+getAgentId(filePath))
	plt.axvline(x=price, color='r')
	plt.ion()

	graph = plt.plot(X,Y)[0]
	line = plt.axvline(x=price, color='r')
	graph.set_ydata(Y)
	axes = plt.gca()
	axes.set_xlim([0, 1])
	axes.set_ylim([-4000, 4000])
	while True:
		newLine = readLatestLine(filePath)
		if prevLine!=newLine:
			prevLine = newLine
		demandList = parseLineToArray(prevLine)
		graph.set_ydata(demandList)
		price = getPrice(logLocation)
		line.set_xdata(price)
		plt.draw()
		plt.pause(updateTime)

if __name__ == "__main__":
	logLocation = sys.argv[2] if len(sys.argv) > 2 else "/Users/iainmeeke/Documents/workspace/logs_powermatcher/"
	fileName = sys.argv[1] if len(sys.argv) > 1 else "bid_1316.csv"
	makeBidCurve(logLocation, fileName)