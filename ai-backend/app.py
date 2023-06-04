import json

from flask import request, Flask
from riskAI.Service.GameService import getGame
from riskAI.Service.MoveService import MoveService

app = Flask(__name__)
service = MoveService()


@app.route('/', methods=['GET'])
def index():
    return "Dit is de server van de ai"


@app.route('/makeMove', methods=['POST'])
# @app.route('/api/makeMove', methods=['GET'])
def move():
    # print(request.get_json())
    # service.Move(getGame())
    # return "Is gefixt :)"

    # Create service.Move task with json data
    service.Move(request.get_json())
    return json.dumps({'success': True}), 200, {'ContentType': 'application/json'}


if __name__ == '__main__':
    from waitress import serve
    print("Starting server ...")
    serve(app, host='0.0.0.0', port=5000)
