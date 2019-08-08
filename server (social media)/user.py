from FriendRequest import FriendRequest
from Post import Post
from constant import large_prime_number as lpn
import exception
import os
import hashlib
from Sender import Sender


class User:
    def __init__(self, username, password, id_param, server):
        self.username = username
        self.friends = []
        self.friends.append(self)
        self.posts = []
        self.friend_requests = []
        self.to_view = []
        self.id = id_param
        self.server = server
        self.salt = self.server.generate_salt()
        self.password_hash = self.generate_hash(password)
        self.caption = None
        self.post_id = None
        self.isPosting = False
        self.file_format = None
        self.ans_sock = None
        os.mkdir('users/' + str(self.id))

    def generate_hash(self, password):
        encoded = bytes(self.salt + password, encoding='utf-8')
        return hashlib.sha256(encoded).hexdigest()

    def check_login(self, password):
        return self.password_hash == self.generate_hash(password)

    def generate_post_id(self):
        return self.id*lpn + len(self.posts)

    def send_friend_request(self, target_id):
        target = self.server.get_user_by_id(target_id)
        new_friend_request = FriendRequest(self, target)
        if self in target.friends:
            raise exception.AlreadyFriend
        if new_friend_request in target.friend_requests:
            raise exception.FriendRequestAlreadySent
        target.friend_requests.append(new_friend_request)

    def accept_friend_request(self, fr_index):
        try:
            fr = self.friend_requests[-fr_index]
        except IndexError:
            raise exception.NonExistingFriendRequest

        fr.accept()

    def deny_friend_request(self, fr_index):
        try:
            fr = self.friend_requests[-fr_index]
        except IndexError:
            raise exception.NonExistingFriendRequest

        fr.deny()

    def begin_post(self, caption, file_format, addr):
        self.post_id = self.generate_post_id()
        self.caption = caption
        self.isPosting = True
        self.file_format = file_format
        new_post = Post(self, self.caption, self.post_id, self.file_format, addr)
        self.posts.append(new_post)
        self.file_format = None
        return new_post.list_sock

    def like(self, post_id):
        post = self.server.get_post_by_id(post_id)
        if self in post.liked_by:
            raise exception.AlreadyLiked
        post.likes += 1
        post.liked_by.append(self)

    def request_post(self, number, addr):
        try:
            post = self.to_view[-number]
        except IndexError:
            raise exception.NonExistingPost
        sender = Sender(self, post, addr)
        sender.start()
        print(self.ans_sock)
        return self.ans_sock

    def number_of_posts(self):
        return len(self.to_view)

    def number_of_friend_requests(self):
        return len(self.friend_requests)

    def get_fr(self, position):
        try:
            fr = self.friend_requests[-position]
        except IndexError:
            raise exception.NonExistingFriendRequest

        return fr.origin
