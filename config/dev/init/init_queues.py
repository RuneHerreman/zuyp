import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
    host="messagebroker.device.dev",
    port=5672,
    credentials=pika.PlainCredentials("guest", "guest"),
))
channel = connection.channel()

# All notifications routed per user via routing key: user-{id}
channel.exchange_declare(
    exchange="zuyp.notifications",
    exchange_type="direct",
    durable=True,
)

print("Exchange declared successfully")
connection.close()
