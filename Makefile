all: 
	kube-create
	sleep 5
	kube-unit

kube-console:
	kubectl exec -it cheftest -- sh -c '/bin/bash'

docker-build:
	docker build -t cheflocal:latest ./docker

kube-create:
	kubectl apply -f ./kubernetes/local.yml

kube-test:
	kubectl exec -it cheftest -- sh -c 'make -f /home/root/Makefile -C $$COOKBOOK_PATH'

kube-lint:
	kubectl exec -it cheftest -- sh -c 'make lint -f /home/root/Makefile -C $$COOKBOOK_PATH'

kube-unit:
	kubectl exec -it cheftest -- sh -c 'make unit -f /home/root/Makefile -C $$COOKBOOK_PATH'

kube-kitchen:
	kubectl exec -it cheftest -- sh -c 'make kitchen -f /home/root/Makefile -C $$COOKBOOK_PATH'

set-kube-secret:
	kubectl create secret generic ami-user --from-file=./username.txt --from-file=./password.txt

kube-cleanup:
	kubectl delete -f ./kubernetes/local.yml
