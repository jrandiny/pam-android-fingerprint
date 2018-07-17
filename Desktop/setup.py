import os
import pyotp
import ConfigParser
import requests
import argparse

def generate_key():
  return os.urandom(40).encode('hex')

# Argument
parser = argparse.ArgumentParser(description='Setup pam-android-fingerprint')
parser.add_argument("-f","--first", action = "store_true", help="Setting up for the first time")
parser.add_argument("-v", "--version", action='version', version='pam-android-fingerprint 0.1')

args = parser.parse_args()

if args.first:
  print "Setting up for the first time"

# Create config
config = ConfigParser.ConfigParser()

# Ask input server
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

config.read('config')
config.add_section('SERVER')
config.set('SERVER', 'url' , server_url)

# setup secret
if args.first:
  secretKey = generate_key()
  queryParam = {"secret":secretKey}
  r = requests.post(server_url+"/store",params=queryParam)

  print "abc"

  config.add_section('SECRET')
  config.set('SECRET', 'secret' , secretKey)

# write config
config.write(open('config', 'w'))
