check-master:
	if [[ `git rev-parse --abbrev-ref HEAD` != "master" ]]; then exit 1; fi

no-unstaged:
	git diff-index --quiet HEAD --  # checks for unstaged/uncomitted files

pull:
	git pull

lint:
	./gradlew clean ktlintCheck detekt

tag:
	git tag "v`grep 'VERSION_NAME' protobuf_java_to_protobufjs/gradle.properties | cut -d'=' -f2 | tr -d '\n'`"
	git push --tags

upload:
	./gradlew :protobuf_java_to_protobufjs:assemble :protobuf_java_to_protobufjs:publish

release: check-master no-unstaged pull lint tag upload
