#!/bin/bash
git checkout master 
git pull origin master
git checkout wail_fix
git rebase master
git checkout master 
git merge wail_fix
git push origin master

echo "Done"
