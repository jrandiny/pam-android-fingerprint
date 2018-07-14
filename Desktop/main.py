import requests
import json
import pyotp
import ConfigParser

DEFAULT_USER = "nobody"
CONFIG_LOC = "/home/joshua/Dev/pam-android-fingerprint/Desktop/config"

def pam_sm_authenticate(pamh, flags, argv):

  config = ConfigParser.ConfigParser()
  config.readfp(open(CONFIG_LOC,'r'))
  server_url = config.get('SERVER', 'url')

  try:
    user = pamh.get_user(None)
  except pamh.exception, e:
    return e.pam_result
  if user == None:
    pamh.user = DEFAULT_USER

  totp = pyotp.TOTP('testSecret')

  r = requests.get(server_url + "/token")
  content = r.json();

  if totp.verify(content["token"]):
    return pamh.PAM_SUCCESS
  else:
    return pamh.PAM_AUTH_ERR

def pam_sm_setcred(pamh, flags, argv):
  return pamh.PAM_SUCCESS

def pam_sm_acct_mgmt(pamh, flags, argv):
  return pamh.PAM_SUCCESS

def pam_sm_open_session(pamh, flags, argv):
  return pamh.PAM_SUCCESS

def pam_sm_close_session(pamh, flags, argv):
  return pamh.PAM_SUCCESS

def pam_sm_chauthtok(pamh, flags, argv):
  return pamh.PAM_SUCCESS
