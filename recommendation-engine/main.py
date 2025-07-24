from fastapi import FastAPI
from controller import test

app = FastAPI()

# Register router
app.include_router(test.router)
