#!/bin/bash
cd ../
brew install boost --with-python
brew install boost-python ffmpeg xerces-c mono
brew cask install java
schemas="$(pwd)/Schemas"
echo "export MALMO_XSD_PATH=$schemas" >> ~/.bashrc
source ~/.bashrc
