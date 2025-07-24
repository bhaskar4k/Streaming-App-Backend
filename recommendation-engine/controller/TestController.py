from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

from entity.TestEntity import Test

router = APIRouter()

class Test(BaseModel):
    name: str
    age: int
    friends: List[str]

@router.get("/hello_python")
def read_root():
    test_app: List[Test] = []

    test_app.append(Test(name="Bhaskar 1", age=25, friends=["Test 1", "Test 2"]))
    test_app.append(Test(name="Bhaskar 2", age=25, friends=["Test 3", "Test 4"]))

    return test_app
