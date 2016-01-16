/**
 *  Thermostat fan cycle
 *
 *  Copyright 2015 Michael Kowalchuk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
  name: "Thermostat fan cycle",
  namespace: "mjk",
  author: "Michael Kowalchuk",
  description: "Runs a thermostat fan for X minutes, then keeps it off for Y minutes. This implementation is designed to be durable in the face of SmartThings' unreliable polling/scheduling.",
  category: "Green Living",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
  section("Title") {
    paragraph "Thermostat fan cycle"
  }
  section("Thermostat") {
    input "thermostat", "capability.thermostat", title:"Select thermostat to be controlled", required: true
    input "length", "number", title:"How long to run the fan (in minutes)", defaultValue:20
    input "delay", "number", title:"How long to wait after running the fan (in minutes)", defaultValue:20
  }
}

def installed() {
  initialize()
}

def updated() {
  unsubscribe()
  unschedule()
   initialize()
}

def initialize() {
  update_fan_state()

  // watchdog events
  runEvery1Hour(update_fan_state)
  subscribe(thermostat, 'temperature', evt_handler)
  subscribe(thermostat, "thermostatOperatingState", evt_handler)
}

def evt_handler(evt) {
  update_fan_state()
}

def update_fan_state() {
  int timeS = now() / 1000
  int lengthS = settings.length.toInteger() * 60
  int delayS = settings.delay.toInteger() * 60
  int runPeriodS = lengthS + delayS
  int timeIntoRunPeriodS = timeS % runPeriodS

  if (timeIntoRunPeriodS >= lengthS) {
    // in delay window
    thermostat.fanAuto()
    schedule_update(runPeriodS - timeIntoRunPeriodS)
  } else {
    // in run window
    thermostat.fanOn()
    schedule_update(lengthS - timeIntoRunPeriodS)
  }
}

def schedule_update(delayS) {
  // Do not use runIn to set up a recurring schedule of less than sixty seconds
  if (delayS < 60) {
    delayS = 60
  }
  runIn(delayS, update_fan_state)  
}
