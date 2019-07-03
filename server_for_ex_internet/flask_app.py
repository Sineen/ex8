import os
from os import path
from flask import Flask, abort, g, jsonify, request, send_file
from uuid import uuid4 as random_uuid
from functools import wraps

app = Flask(__name__)

"""
file content:

topmost: helper functions for db
after: helper functions to handle 'bad' requests
after: the actual endpoints, such as "/" or "/users/<username>/"
last: instructions to run the file in your own computer. (basically un-comment the last lines)

"""


def save_dict(d, name):
    os.chdir(".")
    with open(name, 'w') as f:
        f.write(str(d))


def load_dict(name):
    os.chdir(".")
    if path.exists(name):
        with open(name, 'r') as f:
            return eval(f.read())
    return dict()


def get_or_create_token(username):
    token_to_username = load_dict("token_to_username")
    for k_token, v_username in token_to_username.items():
        if v_username == username:
            return k_token
    new_token = str(random_uuid())
    token_to_username[new_token] = username
    save_dict(token_to_username, "token_to_username")
    return new_token


def get_or_create_user(username):
    username_to_user = load_dict("username_to_user")

    if username in username_to_user:
        return username_to_user[username]

    user = {"username": username,
            "pretty_name": "",
            "image_url": "/images/alien.png"}
    username_to_user[username] = user

    return user


def save_user(user):
    d = load_dict("username_to_user")
    d[user["username"]] = user
    save_dict(d, "username_to_user")


def to_virtual_image_path(image_name):
    # clean
    if not image_name.endswith(".png"):
        image_name = image_name + ".png"
    if "/" in image_name:
        image_name = image_name.split("/")[-1]

    # check
    if path.isfile(image_name):
        return "/images/" + image_name

    # failure
    return None


def required_json_request(next_function):
    @wraps(next_function)
    def checking_func(*args, **kwargs):
        if request.headers['Content-Type'] != 'application/json':
            return jsonify({"error": "Header 'Content-Type' should be 'application/json'"}), 400

        if request.json is None or len(request.json) == 0:
            return jsonify({"error": "no json in request supposed to be a json"}), 400

        return next_function(*args, **kwargs)
    return checking_func


def required_valid_user_token(next_function):
    @wraps(next_function)
    def checking_func(*args, **kwargs):
        header = request.headers.get("Authorization")
        if header is None:
            return jsonify({"error": "no 'Authorization' header found in request"}), 403

        if 'token' not in header:
            return jsonify({"error": "'Authorization' header must be of the string 'token <your token here>'",
                            "example_for_good_authorization": "token 1930482-48291-48292-48294"}), 403

        parts = header.split(" ")
        if (len(parts) != 2) or parts[0] != "token":
            return jsonify({"error": "'Authorization' header must be of the string 'token <your token here>'",
                            "example_for_good_authorization": "token 1930482-48291-48292-48294"}), 403

        token = parts[1]

        token_to_username = load_dict("token_to_username")
        if token not in token_to_username:
            return jsonify({"error": "invalid token"}), 403

        g.username = token_to_username[token]

        return next_function(*args, **kwargs)

    return checking_func


@app.route('/')
def hello_world():
    return jsonify({"data": 'Hello post-pc 2019!'})


@app.route('/users/<username>/token/')
def get_token_for_user(username):
    return jsonify({"data": get_or_create_token(username)})


@app.route('/user/')
@required_valid_user_token
def get_user():
    return jsonify({"data": get_or_create_user(g.username)})


@app.route('/user/edit/', methods=['POST'])
@required_valid_user_token
@required_json_request
def edit_user():
    user = get_or_create_user(g.username)

    k_pretty = "pretty_name"
    if k_pretty in request.json and request.json[k_pretty] is not None:
        user[k_pretty] = str(request.json[k_pretty])
    elif k_pretty in request.json:
        user[k_pretty] = ""

    k_image = "image_url"
    if k_image in request.json and request.json[k_image] is not None:
        image_path = to_virtual_image_path(request.json[k_image])
        if image_path is not None:
            user[k_image] = image_path

    save_user(user)
    return jsonify({"data": user})


@app.route("/images/<image_name>.png")
def get_image(image_name):
    os.chdir(".")
    image_name = image_name.lower()
    if path.isfile(image_name + ".png"):
        return send_file(image_name + ".png", mimetype="image/png")


@app.route("/images/all/")
def get_all_available_images():
    os.chdir(".")
    images = [f for f in os.listdir() if ".png" in f]
    images_virtual_urls = ["/images/" + f for f in images]
    return jsonify({"data": images_virtual_urls})




def get_local_ip():
    import socket
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    local_ip = s.getsockname()[0]
    s.close()
    return local_ip


 #
 # uncomment those lines to run locally:
if __name__ == '__main__':
    print("waking up server...")
    print("NOTICE: just 1 instance of the file flask_app.py can run a time")
    print(
        "NOTICE: to connect to this server from a client (android device or web browser) your client needs to be "
        "connected to the same wifi network as this server (the computer running flask_app.py)")
    print("")
    print("the server base url is:")
    print("http://" + get_local_ip() + ":5678/")
    print("")
    print("")
    print("")
    app.debug = True
    app.run(host='0.0.0.0', port=5678)
#
# after the app is running, you can call it from any desktop browser or android device
# the target url will be http://<your-computer's-ip>:5678/
# instead of http://hujipostpc2019.pythonanywhere.com/
#
# IMPORTANT: the client web-browser \ android-device should be connected to the same wifi as the server (your computer)!
