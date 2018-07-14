import os
import pyotp
import ConfigParser
import requests

def generate_key():
  return os.urandom(40).encode('hex')

# Create config
config = ConfigParser.ConfigParser()

# Ask input
while True:
  server_url = raw_input("Server url : ")

  # Check
  try:
    r = requests.get(server_url + "/identity")
    content = r.json();
    if content["identity"] == "Fingerprint Server":
      break
    else:
      print "URL Invalid"
  except:
    print "URL Invalid"


# write config
config.read('config')
config.add_section('SERVER')
config.set('SERVER', 'url' , server_url)
config.write(open('config', 'w'))
