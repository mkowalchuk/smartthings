
definition(
  name: "Remote Polling Bridge",
  namespace: "mjk",
  author: "michael.kowalchuk@gmail.com",
  description: "Enable remote polling.",
  category: "Convenience",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  oauth: [displayName: "Remote Polling Bridge", displayLink: "http://localhost:4567"]
)

preferences {
  input "pollDevices", "capability.polling", title:"Select devices to be polled", multiple:true, required:false
}

mappings {
  path("/poll") {
    action: [
      POST: "poll"
    ]
  }
}

def poll() {
  pollDevices.poll()
}

def installed() {
}

def updated() {
}
