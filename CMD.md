```bash
sudo apt-get remove --purge gitwit -y
```

```bash
sudo apt-get install ./build/packager/gitwit_1.0.0-RC2.deb
```

```bash
./gradlew clean packageLinux
```

```bash
jdeps --print-module-deps --ignore-missing-deps --recursive ./build/libs/gitwit-1.0.0-RC2.jar

```
```bash
jdeps --list-deps --ignore-missing-deps ./build/libs/gitwit-1.0.0-RC2.jar

```
