#!/bin/sh
#Repository root path
repository_path=`git rev-parse --show-toplevel`
#local hook paths
hook_projectfilename='projectName.txt'
hook_filename='commit-msg'
local_git_hook_dir=$repository_path'/.git/hooks'
local_hook_file_path=$local_git_hook_dir'/'$hook_filename
local_hook_project_file_path=$local_git_hook_dir'/'$hook_projectfilename
#Path to commit-checker script
custom_hook_file=$hook_filename
custom_hook_path=$repository_path'/git_hooks/'$custom_hook_file
custome_hook_project_file_path=$repository_path'/git_hooks/'$hook_projectfilename
#Creating symlink between local hook and custom hook
`ln -s -f $custome_hook_project_file_path $local_hook_project_file_path`
`ln -s -f $custom_hook_path $local_hook_file_path`
`chmod +x $local_hook_file_path`
