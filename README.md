# PAM Android Fingerprint

PAM authentication module using Android fingerprint sensor

## Android

Compile and install android app using gradle

## Desktop

### Dependencies
`sudo apt install python-pyotp python-pam python-requests`

### Setting up
 1. Copy `setup,py` and `main.py` to the same folder
 2. Edit PAM configuration file (usually located under `/etc/pam.d/`)
 3. Add auth entry (`auth <pam control flag> pam_python.so <location of main.py>`)
 4. Run `setup.py -f` for first time setup (make sure the Android server is running)

## Usage

![PAM Android Fingerprint in action](action.gif)