"""Functional smoke check against the local Auth emulator; never contacts Firebase production."""
import json
import urllib.request
import urllib.error
import uuid

BASE = 'http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1/accounts:'

def post(action, body):
    request = urllib.request.Request(BASE + action + '?key=demo-emulator-api-key',
        data=json.dumps(body).encode(), headers={'Content-Type': 'application/json'})
    with urllib.request.urlopen(request, timeout=10) as response:
        return json.load(response)

email = 'smoke-' + uuid.uuid4().hex + '@example.test'
password = uuid.uuid4().hex
account = post('signUp', {'email': email, 'password': password, 'returnSecureToken': True})
try:
    login = post('signInWithPassword', {'email': email, 'password': password, 'returnSecureToken': True})
    assert login['localId'] == account['localId'], 'Login returned a different user'
    lookup = post('lookup', {'idToken': login['idToken']})
    assert lookup['users'][0]['email'] == email, 'Account was not retained'
    try:
        post('signInWithPassword', {'email': email, 'password': password + 'wrong', 'returnSecureToken': True})
    except urllib.error.HTTPError as error:
        assert error.code == 400, 'Unexpected authentication response'
    else:
        raise AssertionError('Wrong password was accepted')
    print('PASS: registration, login, retained identity and wrong-password rejection (Auth emulator).')
finally:
    post('delete', {'idToken': account['idToken']})
