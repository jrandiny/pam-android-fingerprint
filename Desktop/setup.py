import os
import pyotp
import ConfigParser
import requests
import argparse
import base64

def generate_key():
  return base64.b32encode(os.urandom(40))

# Argument
parser = argparse.ArgumentParser(description="Setup pam-android-fingerprint")
parser.add_argument("-f","--first", action = "store_true", help="Setting up for the first time")
parser.add_argument("-v", "--version", action="version", version="pam-android-fingerprint 0.1")

args = parser.parse_args()

if args.first:
  print "Setting up for the first time"
  if os.path.isfile("config"):
    os.remove("config")

# Create config
config = ConfigParser.ConfigParser()

# Ask input server
while True:
  server_url = raw_input("Server location : ")

  #Make lower
  server_url = server_url.lower()

  # check if port exist
  if not ":" in server_url:
    server_port = raw_input("Server port : ")
    server_url = server_url + ":" + server_port

  # Add http
  if(server_url[:4] != "http"):
    server_url = "http://" + server_url

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

config.read("config")
if(not(config.has_section("SERVER"))):
  config.add_section("SERVER")

config.set("SERVER", "url" , server_url)

# setup secret
if args.first:
  secretKey = generate_key()
  queryParam = {"secret":secretKey}
  r = requests.post(server_url+"/store",params=queryParam,timeout=30)

  print "Verify on phone"

  config.add_section("SECRET")
  config.set("SECRET", "secret" , secretKey)

# write config
config.write(open("config", "w"))

print "Setup success"
