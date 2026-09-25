.PHONY: install test run clean

install:
	@mvn -B -q -DskipTests compile

test:
	@mvn -B verify

run:
	@mvn -q -DskipTests compile exec:java

clean:
	@mvn -q clean
