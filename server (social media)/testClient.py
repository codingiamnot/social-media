import json
import socket

# enter the server ip
server_ip = ''
server_socket = (server_ip, 1234)


def register(username, password):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    data = {'action': 'register', 'username': username, 'password': password}
    data = json.dumps(data)
    sock.sendall(data.encode())
    response = sock.recv(1024)
    print(json.loads(response))
    sock.close()


def login(username, password):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    data = {'action': 'login', 'username': username, 'password': password}
    data = json.dumps(data)
    sock.sendall(data.encode())
    response = sock.recv(1024)
    print(json.loads(response))
    sock.close()


def post():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    data = {'action': 'post', 'caption': '', 'format': 'jpg'}
    data = json.dumps(data)
    sock.sendall(data.encode())
    response = sock.recv(1024)
    response = json.loads(response)
    print(response)
    list_port = response['port']
    sock.close()

    print(list_port)

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    img_file = open('house.png', 'rb')
    data = img_file.read(1024)
    while data:
        sock.sendall(data)
        data = img_file.read(1024)

    img_file.close()
    sock.close()


def send_friend_request(target_id):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    data = {'action': 'send_friend_request', 'target_id': target_id}
    data = json.dumps(data)
    sock.sendall(data.encode())
    response = sock.recv(1024)
    response = json.loads(response)
    print(response)


def accept_friend_request(fr_index):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)
    data = {'action': 'accept_friend_request', 'fr_index': fr_index}
    data = json.dumps(data)
    sock.sendall(data.encode())
    response = sock.recv(1024)
    response = json.loads(response)
    print(response)


def request_post():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(server_socket)


register('admin', '1234')
register('admin2', '1234')
login('admin', '1234')
send_friend_request(1)
login('admin', '1234')
post()
post()
post()

