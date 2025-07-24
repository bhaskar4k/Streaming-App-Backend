from fastapi import FastAPI
from controller import TestController

app = FastAPI()

# Register router
app.include_router(TestController.router)
