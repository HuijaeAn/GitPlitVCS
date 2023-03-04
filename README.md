# GitPlit - Version Control System
GitPlit is a version control system that reproduces the features of git.
## Table of Contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
Gitplit is a version control system that mimics git.  
It supports the following features to help the user keep track of file changes.    
   
- [Init](#init)
- [Add](#add)
- [Commit](#commit)
- [Log](#log)
- [Branch](#branch)
- [Checkout](#checkout)
- [Rm](#rm)
- [Find](#find)
- [Status](#status)
- [Reset](#reset)
- [Merge](#merge)
- [Clone](#clone)
### Init

```
java gitplit.Main init
```
Initializes a GitPlit repository.

![image](https://user-images.githubusercontent.com/126933771/222881095-1d55e66b-25d6-4e24-a0f8-752873e91f2d.png)    
     
A GitPlit repository consists of 4 directories and 2 files.   
     
* __additions__ directory is a storage of all files staged for addition.    
* __branches__ directory is a storage of branches.    
* __commits__ directory is a storage of all commits ever made.    
* __removals__ directory is a storage of all files staged for removal.     
* __current_branch__ is a file that stores the name of the current branch.    
* __head_commit__ is a file that stores the head commit ID.

### Add 

```
java gitplit.Main add [FileName]
```
Adds a file with __FileName__ to the addition staging area.   

![image](https://user-images.githubusercontent.com/126933771/222881454-d5904823-fa57-4d16-af87-e6483747885d.png)    
![image](https://user-images.githubusercontent.com/126933771/222881472-b83e19dc-1945-433c-ac29-bb97af4c298a.png)    

If the file does not exist in the current working directory, prints the following message instead.   
     
![image](https://user-images.githubusercontent.com/126933771/222881637-076dd8db-d29c-40f1-b13c-4a6e962835eb.png)      


### Commit

```
java gitplit.Main commit [Message]
```
Creates a commit with the commit message __Message__.   

![image](https://user-images.githubusercontent.com/126933771/222881699-04481c48-a626-45bf-9e11-22b5d6e96d64.png)   
![image](https://user-images.githubusercontent.com/126933771/222881726-0b95e1d6-564e-4281-9e43-6c14255a2fe0.png)    
    
Creating commit clears the addition/removal staging area.     
Commit information is serialized and written as a content in each commit file.   
    
![image](https://user-images.githubusercontent.com/126933771/222881960-3b8d968e-b39e-4a33-99c3-c7b21f08f8d9.png)    
   
### Log

```
java gitplit.Main log
```
Displays the commit tree of the GitPlit repository.
   
![image](https://user-images.githubusercontent.com/126933771/222882150-76057bf3-9149-43a0-aa06-346463956e05.png)   
   
### Branch
   




### Checkout

```
java gitplit.Main checkout [BranchName]
java gitplit.Main checkout -- [FileName]
java gitplit.Main checkout [CommitID] -- [FileName]
```

   








