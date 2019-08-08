import random
import string
import exception
from threading import Thread
from user import User
from constant import large_prime_number as lpn
from tcpServer import TcpServer
import os
import shutil


class Server(Thread):
    def __init__(self, port=1234, clear_users=True):
        Thread.__init__(self)
        self.users = []
        self.active_users = {}
        self.tcp_server = TcpServer(port, self)
        self.tcp_server.start()
        self.is_listening = True
        self.accepted_img_formats = ['png', 'jpg', 'jpeg']
        try:
            os.mkdir('users')
        except FileExistsError:
            if clear_users:
                shutil.rmtree('users')
                os.mkdir('users')

    def run(self):
        while self.is_listening:
            pass
        self.tcp_server.is_listening = False

    def get_user_by_id(self, user_id):
        if user_id >= len(self.users):
            raise exception.NonExistingUser
        return self.users[user_id]

    def get_post_by_id(self, post_id):
        user = self.get_user_by_id(int(post_id/lpn))
        if post_id % lpn >= len(user.posts):
            raise exception.NonExistingPost
        return user.posts[post_id % lpn]

    def generate_salt(self, string_length=10):
        components = string.ascii_letters + string.digits + string.punctuation
        ans = ''
        for i in range(string_length):
            ans = ans + random.choice(components)
        return ans

    def register(self, username, password):
        for user in self.users:
            if user.username == username:
                raise exception.UsedUsername
        new_user = User(username, password, len(self.users), self)
        self.users.append(new_user)

    def login(self, username, password, ip):
        for user in self.users:
            if user.username == username:
                if user.generate_hash(password) == user.password_hash:
                    self.active_users[ip] = user
                    return
                raise exception.IncorrectLogin
        raise exception.IncorrectLogin

    def like(self, post_id, ip):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        self.active_users[ip].like(post_id)

    def send_friend_request(self, ip, target_id):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        self.active_users[ip].send_friend_request(target_id)

    def accept_friend_request(self, ip, fr_index):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        self.active_users[ip].accept_friend_request(fr_index)

    def deny_friend_request(self, ip, fr_index):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        self.active_users[ip].deny_friend_request(fr_index)

    def begin_post(self, caption, file_format, addr):
        ip = addr[0]
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        if file_format not in self.accepted_img_formats:
            raise exception.NotSupportedFormat
        return self.active_users[ip].begin_post(caption, file_format, addr)

    def request_post(self, number, addr):
        ip = addr[0]
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        return self.active_users[ip].request_post(number, addr)

    def number_of_posts(self, ip):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        return self.active_users[ip].number_of_posts()

    def number_of_friend_requests(self, ip):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        return self.active_users[ip].number_of_friend_requests()

    def get_fr(self, position, ip):
        if ip not in self.active_users:
            raise exception.NotLoggedIn
        return self.active_users[ip].get_fr(position)


if __name__ == '__main__':
    curr_server = Server()
    curr_server.start()
    while True:
        cmd = input()
        if cmd == 'quit':
            curr_server.is_listening = False
            break

