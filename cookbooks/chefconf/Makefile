all: lint unit kitchen

lint:
	chef exec cookstyle
	chef exec foodcritic . -t ~FC078 -t ~FC064

unit:
	chef exec rspec

kitchen:
	chef exec kitchen converge
	chef exec kitchen verify

package:
	chef exec berks install
	chef exec berks package

upload:
	# insert your package upload logic here
	
cleanup:
	chef exec kitchen destroy