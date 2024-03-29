import os
import requests

token = os.environ["MODRINTH_API"]

def patch(path, body):
    return requests.patch("https://api.modrinth.com/v2/" + path, headers={"Authorization": token, "User-Agent": "kinderhead/luadatapack"}, json=body)

readme = ""
with open("README.md", "r") as f:
    readme = f.read()

if (patch("project/bgUHPLzW", {"body": readme}).status_code > 400):
    exit(1)
