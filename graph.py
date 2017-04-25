import csv
import os
from collections import OrderedDict

import plotly.plotly as py
import plotly.graph_objs as go
from dateutil.parser import parse
import datetime as dt
from pprint import pprint

class pvDemand:
	def __init__(self):
		self.timeIndex = 3
		self.demandIndex = 4
		self.demandDict = dict()
		self.name = "pv energy produced"

class evDemand:
	def __init__(self):
		self.timeIndex = 3
		self.demandIndex = 6
		self.demandDict = dict()
		self.name = "ev demand"

#need a generic object to just hold a dictionary
class genObj:
	def __init__(self):
		self.demandDict = dict()

def createTrace(logObj, traceName = None):
	ordered = OrderedDict(sorted(logObj.demandDict.items()))
	x_data = list(ordered.keys())
	y_data = list(ordered.values())
	if traceName == None:
		name = logObj.name
	else:
		name = traceName
	# Create a trace
	trace = go.Scatter(x = x_data, y=y_data, mode = 'lines', name = name )
	return trace




def readFile(filePath, logObj):
	with open(filePath, 'rb') as f:
		reader = csv.reader(f)
		readerList = list(reader)
		readerList.pop(0) #remove header
		##parse the first date and round to the nearest minute
		firstDate = parse(readerList[0][logObj.timeIndex])
		firstDate = firstDate - dt.timedelta(seconds=firstDate.second, microseconds=firstDate.microsecond)

		currentDate = firstDate
		highestDemand = float(readerList[1][logObj.demandIndex])
		##loop through the full output and get the highest demand for each minute
		for row in readerList:
			rowDate = parse(row[logObj.timeIndex])
			rowDate = rowDate - dt.timedelta(seconds=rowDate.second, microseconds=rowDate.microsecond)
			rowDemand = float(row[logObj.demandIndex])
			##if we have moved to the next minute then add the values from the previous
			if currentDate != rowDate:
				if currentDate in logObj.demandDict:
					logObj.demandDict[currentDate] = logObj.demandDict[currentDate] + highestDemand
				else:
					logObj.demandDict[currentDate] = highestDemand
				currentDate = rowDate #set current date to next minute
				highestDemand = 0.0 #reset highestDemand
			if rowDemand > highestDemand:
				highestDemand = rowDemand


##currently converting to watthours, might change this depening on size of value
def convertToWh(logObj):
	#E(kWh) = P(W)*t(hr)/1000

	for key, value in logObj.demandDict.iteritems():
		newValue = value * (.017)
		logObj.demandDict[key] = newValue

def directoryIterate(directory_name, logObj):
	for filename in os.listdir(directory_name):
		if filename.endswith(".csv"):
			readFile(directory_name+"/"+filename, logObj)

#subtract the load from the source to get the total load on the system
def difference(load, source):
	result = genObj()
	for key,value in load.demandDict.iteritems():
		if key in source.demandDict:
			result.demandDict[key] = value - source.demandDict[key]
	return result

if __name__ == "__main__":
	ev = evDemand()
	pv = pvDemand()
	directoryIterate("/Users/iainmeeke/Documents/workspace/logs_powermatcher/pv", pv)
	directoryIterate("/Users/iainmeeke/Documents/workspace/logs_powermatcher/ev", ev)
	convertToWh(ev)

	totalDemand = difference(ev, pv)
	trace0 = createTrace(pv)
	trace1 = createTrace(ev)
	trace3 = createTrace(totalDemand, "Total Demand")

	data = [trace0, trace1, trace3]
	py.plot(data, filename='line-mode', sharing='secret')
