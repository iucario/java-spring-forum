import re
import time
from faker import Faker
from passlib.context import CryptContext

fake = Faker()

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
name_pattern = r"^[a-zA-Z0-9_]{3,20}$"
password_pattern = r"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!()])(?=\S+$).{8,}$"


def hash_password(password):
    return pwd_context.hash(password)


def check_user(user: dict):
    if not re.match(name_pattern, user["name"]):
        raise ValueError("Invalid name", user["name"])
    if not re.match(password_pattern, user["password"]):
        raise ValueError("Invalid password", user["password"])
    if not isinstance(user["created_at"], int):
        raise ValueError("Invalid created_at", user["created_at"])


def gen_users():
    for _ in range(10):
        name = fake.user_name()
        password = fake.password()
        dt = fake.date_time_between(start_date="-10y", end_date="now")
        ts = int(time.mktime(dt.timetuple()) * 1000)
        user = {
            "name": name,
            "password": password,
            "hashed_password": hash_password(password),
            "created_at": ts,
        }
        check_user(user)
        yield user


if __name__ == "__main__":
    import json

    print(json.dumps(list(gen_users()), indent=2))
