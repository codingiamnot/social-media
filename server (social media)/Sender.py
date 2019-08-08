from threading import Thread
import socket
import json
import time


class Sender(Thread):
    def __init__(self, user, post, addr):
        Thread.__init__(self)
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.local_ip = socket.gethostbyname(socket.gethostname())
        self.sock.bind((self.local_ip, 0))
        self.sock.setblocking(True)
        self.sock.settimeout(5)
        self.sock.listen(1)
        self.addr = addr
        self.nr_timeouts = 0
        self.ip = addr[0]
        self.post = post
        user.ans_sock = (self.local_ip, self.sock.getsockname()[1])

    def run(self):
        # send json
        while self.nr_timeouts <= 5:
            try:
                conn, adr = self.sock.accept()
            except socket.timeout:
                self.nr_timeouts += 1
            else:
                # dumb bug where the sock is once read as 127.0.0.1 and once as real ip
                dumb_bug = (adr[0] == '127.0.0.1' and self.addr[0] == self.ip) or \
                           (self.addr[0] == '127.0.0.1' and adr[0] == self.ip)
                if not adr[0] == self.ip and not dumb_bug:
                    conn.close()
                    return
                print('conn made')
                data = {}
                data['post_id'] = self.post.id
                data['op_username'] = self.post.op.username
                data['caption'] = self.post.caption
                data['file_format'] = self.post.file_format
                data['likes'] = self.post.likes
                conn.sendall(json.dumps(data).encode())
                conn.close()
                break

        self.nr_timeouts = 0
        conn = None

        # send image
        while self.nr_timeouts <= 5:
            try:
                conn, adr = self.sock.accept()
            except socket.timeout:
                self.nr_timeouts += 1
            else:
                # dumb bug where the sock is once read as 127.0.0.1 and once as real ip
                dumb_bug = (adr[0] == '127.0.0.1' and self.addr[0] == self.ip) or \
                            (self.addr[0] == '127.0.0.1' and adr[0] == self.ip)
                if not adr[0] == self.ip and not dumb_bug:
                    conn.close()
                    return
                print('conn made')
                post_file = open(self.post.link, 'rb')
                img = post_file.read(1024)
                while img:
                    conn.sendall(img)
                    img = post_file.read(1024)
                break
