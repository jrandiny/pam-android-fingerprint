import requests
import pyotp
import ConfigParser
import sys
import os

DEFAULT_USER = "nobody"
CONFIG_LOC = "/home/joshua/Dev/pam-android-fingerprint/Desktop/config"


def pam_sm_authenticate(pamh, flags, argv):

    if pamh.service == "polkit-1":
        OUTPUT = False
        sys.stdout = open(os.devnull, 'w')

    print "Preparing to authenticate using fingerprint"

    config = ConfigParser.ConfigParser()
    config.readfp(open(CONFIG_LOC, 'r'))
    server_url = config.get('SERVER', 'url')
    secret = config.get('SECRET', 'secret')

    try:
        user = pamh.get_user(None)
    except pamh.exception, e:
        return e.pam_result

    if user == None:
        pamh.user = DEFAULT_USER

    totp = pyotp.TOTP(secret)

    print "Requesting fingerprint from " + server_url

    try:
        r = requests.get(server_url + "/identity", timeout=1)
        content = r.json()

    except:
        print "Phone not connected"
        return pamh.PAM_AUTH_ERR

    if content["identity"] != "Fingerprint Server":
        print "Phone not connected"
        return pamh.PAM_AUTH_ERR

    try:
        r = requests.get(server_url + "/token", timeout=30)
        content = r.json()
    except:
        print "Connection error"
        return pamh.PAM_AUTH_ERR

    if totp.verify(content["token"]):
        print "Authenticated using fingerprint"
        return pamh.PAM_SUCCESS
    else:
        print "Authentication failed"
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
