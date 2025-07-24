from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI()

class Test(BaseModel):
    name: str
    age: int
    friends: List[str]



@app.get("/hello_python")
def read_root():
    test_app: List[Test] = []

    test_app.append(Test(name="Bhaskar 1", age=25, friends=["Test 1, Test 2"]))
    test_app.append(Test(name="Bhaskar 2", age=25, friends=["Test 3, Test 3"]))
    return test_app
