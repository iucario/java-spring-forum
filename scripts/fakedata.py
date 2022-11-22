import re
import time
from faker import Faker
from passlib.context import CryptContext
from datetime import datetime
import random
import json

fake = Faker()

pwd_context = CryptContext(schemes=['bcrypt'], deprecated='auto')
name_pattern = r'^[a-zA-Z0-9_]{3,20}$'
password_pattern = r'^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!()])(?=\S+$).{8,}$'


def hash_password(password):
    return pwd_context.hash(password)


def ts_to_datetime(ts: int):
    return datetime.fromtimestamp(ts / 1000)


def check_user(user: dict):
    if not re.match(name_pattern, user['name']):
        raise ValueError('Invalid name', user['name'])
    if not isinstance(user['created_at'], int):
        raise ValueError('Invalid created_at', user['created_at'])


def gen_users(n: int = 10):
    """Generate n users created between last 10 and 1 year"""
    for i in range(n):
        name = fake.user_name()
        password = fake.password()
        dt = fake.date_time_between(start_date='-10y', end_date='-1y')
        ts = int(time.mktime(dt.timetuple()) * 1000)
        user = {
            'id': i + 1,
            'name': name,
            'hashed_password': hash_password(password),
            'created_at': ts,
        }
        check_user(user)
        yield user


def gen_posts(n: int = 3):
    '''Generate n posts per user created in last one year'''
    for j, user in enumerate(gen_users()):
        for i in range(n):
            title = fake.sentence(nb_words=6, variable_nb_words=True)
            body = fake.text(max_nb_chars=200)
            created_at = fake.date_time_between(start_date='-1y',
                                                end_date='now')
            ts = int(time.mktime(created_at.timetuple()) * 1000)
            yield {
                'id': i + 1 + j * n,
                'title': title,
                'body': body,
                'created_at': ts,
                'updated_at': ts,
                'active_at': ts,
                'user_id': user['id'],
            }


def gen_comments(n: int = 10, num_users: int = 10):
    '''Generate n comments per post. Randomly assign users to comments'''
    for j, post in enumerate(gen_posts()):
        for i in range(n):
            body = fake.text(max_nb_chars=200)
            start_date = ts_to_datetime(post['created_at'])
            created_at = fake.date_time_between(start_date=start_date,
                                                end_date='now')
            ts = int(time.mktime(created_at.timetuple()) * 1000)
            yield {
                'id': i + 1 + j * n,
                'body': body,
                'created_at': ts,
                'updated_at': ts,
                'post_id': post['id'],
                'user_id': fake.random_int(min=1, max=num_users),
            }


def gen_fav_user_post(n: int, num_users: int, num_posts: int):
    """Generate n pairs of user_id and post_id"""
    index = 1
    for user_id in range(1, 1 + num_users):
        inds = random.choices(range(1, 1 + num_posts), k=n)
        for post_id in set(inds):
            yield {'id': index, 'user_id': user_id, 'post_id': post_id}
            index += 1


def gen_user_stats(num_users: int, num_posts: int, num_comments: int):
    for user_id in range(1, 1 + num_users):
        yield {
            'user_id': user_id,
            'post_count': num_posts,
            'comment_count': num_comments,
            'file_count': 0,
        }


def to_sql(table: str, d: dict):
    keys = ', '.join(d.keys())
    values = ', '.join([f"'{v}'" for v in d.values()])
    s = f'insert into {table} ({keys}) values ({values});'
    return s


def fix_sequence():
    tables = {
        'users': 'user_seq',
        'posts': 'post_seq',
        'comments': 'comment_seq',
        'fav_user_post': 'fav_user_post_seq',
        'user_stats': 'user_stats_seq',
    }
    for table, seq in tables.items():
        yield f"select setval('{seq}', (select max(id) from {table}) + 1);"


def main():
    data = 'data.sql'
    num_users = 10
    num_post_per_user = 3
    num_comment_per_post = 3
    user_stats = dict((user_id, {
        'id': user_id,
        'user_id': user_id,
        'post_count': num_post_per_user,
        'comment_count': 0,
        'file_count': 0
    }) for user_id in range(1, 1 + num_users))

    with open(data, 'w') as f:
        for user in gen_users(num_users):
            f.write(to_sql('users', user))
            f.write('\n')
        for post in gen_posts(num_post_per_user):
            f.write(to_sql('posts', post))
            f.write('\n')
        for comment in gen_comments(num_comment_per_post):
            f.write(to_sql('comments', comment))
            f.write('\n')
            user_stats[comment['user_id']]['comment_count'] += 1
        for favorite in gen_fav_user_post(n=3,
                                          num_users=num_users,
                                          num_posts=num_post_per_user *
                                          num_users):
            f.write(to_sql('fav_user_post', favorite))
            f.write('\n')
        for stat in user_stats.values():
            f.write(to_sql('user_stats', stat))
            f.write('\n')
        for s in fix_sequence():
            f.write(s)
            f.write('\n')


if __name__ == '__main__':

    print(json.dumps(list(gen_users()), indent=2))
    print(to_sql('users', next(gen_users())))
    print(to_sql('posts', next(gen_posts())))
    print(to_sql('comments', next(gen_comments())))
    print(to_sql('favorites', next(gen_fav_user_post(1, 10, 10))))
    print('\n'.join(x for x in fix_sequence()))

    main()
