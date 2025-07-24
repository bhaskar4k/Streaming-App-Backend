from pydantic import BaseModel
from typing import List

class Test(BaseModel):
    name: str
    age: int
    friends: List[str]