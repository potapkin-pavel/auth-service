# scripts

docker-compose down -v
# stop and remove all containers, networks, images, and volumes
# (the -v flag will remove the named volumes as well)

docker-compose up -d
# start the containers
# (the -d flag will run the containers in the background)

docker-compose up -d --no-deps --build app
# build and start the app container
# (the --no-deps flag will not start any other containers)
# (the --build flag will build the app container)

