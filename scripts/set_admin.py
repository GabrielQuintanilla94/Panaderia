"""Run in a trusted admin environment with Application Default Credentials.
Usage: python scripts/set_admin.py PROJECT_ID FIREBASE_UID
Requires: pip install firebase-admin
Never include a service account private key in the Android application.
"""
import sys
import firebase_admin
from firebase_admin import auth

if len(sys.argv) != 3:
    raise SystemExit("Uso: python scripts/set_admin.py PROJECT_ID FIREBASE_UID")
firebase_admin.initialize_app(options={"projectId": sys.argv[1]})
user = auth.get_user(sys.argv[2])
claims = dict(user.custom_claims or {})
claims["admin"] = True
auth.set_custom_user_claims(user.uid, claims)
print("Permiso administrativo asignado. Cierre sesión y vuelva a ingresar.")
