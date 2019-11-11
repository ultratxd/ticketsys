read -p "input old image version?" version
echo $version

docker build --rm -f Dockerfile -t cj-ticketsys:$version .
docker rmi 172.28.3.45:5000/cj-ticketsys:$version
docker tag cj-ticketsys:$version 172.28.3.45:5000/cj-ticketsys:$version
docker push 172.28.3.45:5000/cj-ticketsys:$version
