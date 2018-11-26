#!/usr/bin/env python

import json
import os
from pprint import pprint

with open('app.json') as json_data:
    vars = json.load(json_data)
pprint(vars)

# https://stackoverflow.com/questions/89228/calling-an-external-command-in-python

for k in vars.keys():
    command = "heroku config:set " + k + "=" + vars[k];
    print("executing: " + command);
    os.system(command);
