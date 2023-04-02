# GitPlit - Version Control System
GitPlit is a customizable version control system inspired by git, designed to streamline software development.  
## Table of Contents
* [General Information](#general-information)
* [Features](#features)
* [Technologies](#technologies)  
  
## General Information
GitPlit is a powerful version control system designed to streamline your software development process.  
  
Based on the popular git protocol, GitPlit offers all of the core functionality you need to manage your source code, collaborate with team members, and track changes over time. With GitPlit, you can easily create branches, commit changes, merge code, and resolve conflicts - all from a single, user-friendly interface.  
  
Check out the features section to see how GitPlit can help take your development workflow to the next level!
   
## Features
- [Init](#init)
- [Add](#add)
- [Commit](#commit)
- [Log](#log)
- [Branch](#branch)
- [Status](#status)
- [Rm](#rm)
- [Find](#find)
- [Checkout](#checkout)
- [Reset](#reset)
- [Merge](#merge)
- [Clone](#clone)
  
‎   
## Init

```
java gitplit.Main init
```
Initializes a GitPlit repository and creates the first commit.

![image](https://user-images.githubusercontent.com/126933771/222881095-1d55e66b-25d6-4e24-a0f8-752873e91f2d.png)    
     
A GitPlit repository consists of 4 directories and 2 files.   
     
* __additions__ directory stores all files staged for addition.    
* __branches__ directory stores branches.    
* __commits__ directory stores all commits ever made.    
* __removals__ directory stores all files staged for removal.     
* __current_branch__ file stores the name of the current branch.    
* __head_commit__ file stores the head commit ID.
  
‎   
## Add 

```
java gitplit.Main add [FileName]
```
Adds a file with __FileName__ to the addition staging area.   

![image](https://user-images.githubusercontent.com/126933771/222881454-d5904823-fa57-4d16-af87-e6483747885d.png)    
  
![image](https://user-images.githubusercontent.com/126933771/222881472-b83e19dc-1945-433c-ac29-bb97af4c298a.png)    

If the file does not exist in the current working directory, prints the following message instead.   
     
![image](https://user-images.githubusercontent.com/126933771/222881637-076dd8db-d29c-40f1-b13c-4a6e962835eb.png)      
  
‎   
## Commit

```
java gitplit.Main commit [Message]
```
Creates a commit with the commit message __Message__.   

![image](https://user-images.githubusercontent.com/126933771/222881699-04481c48-a626-45bf-9e11-22b5d6e96d64.png)   
  
![image](https://user-images.githubusercontent.com/126933771/222881726-0b95e1d6-564e-4281-9e43-6c14255a2fe0.png)    
       
Commit information is serialized and written in each commit file.   
    
![image](https://user-images.githubusercontent.com/126933771/228278087-a435eca6-11ca-44b3-8b43-df48b3095d81.png)   
  
‎   
## Log

```
java gitplit.Main log
```
Displays the commit history.
   
![image](https://user-images.githubusercontent.com/126933771/222882150-76057bf3-9149-43a0-aa06-346463956e05.png)   
  
‎   
## Branch
   
```
java gitplit.Main branch [BranchName]
```
Creates a branch with __BranchName__.  
  
![image](https://user-images.githubusercontent.com/126933771/227720783-138bf92e-2751-4533-82d4-e9830dbd066f.png)   
  
![image](https://user-images.githubusercontent.com/126933771/227720814-a999f096-40ad-45c9-b3e4-025a13969fa9.png)  
  
Each branch file includes the ID of the commit it's pointing at. 
  
![image](https://user-images.githubusercontent.com/126933771/228278603-ed6ad2c8-e4a2-4b96-8cc8-04455393f83a.png)  
‎      
‎  

```
java gitplit.Main branch -d [BranchName]
```
Removes a branch with __BranchName__.  
  
![image](https://user-images.githubusercontent.com/126933771/227721168-5239ff5a-2b8d-4645-affe-4d3a89291c37.png)  
  
![image](https://user-images.githubusercontent.com/126933771/227721201-440a18ea-d367-4d4f-939f-a7ccb0fa660b.png)  
  
‎   
## Status

```
java gitplit.Main status
```
Displays the current status of the working directory.  
   
![image](https://user-images.githubusercontent.com/126933771/228248968-0eafe08b-0d2a-4d51-a417-ec1d4d9a666f.png)   
  
![image](https://user-images.githubusercontent.com/126933771/228249099-ef51ea09-bc2c-4964-8039-f4ea9628d018.png)
  
‎   
## Rm

```
java gitplit.Main rm [FileName]
```

Removes a file with __FileName__ from the CWD and puts it in the removal staging area.  

![image](https://user-images.githubusercontent.com/126933771/228251033-1399ac21-02fa-4c47-ae3e-115c6ff8ae0a.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228251662-aee92902-fc33-4c42-a0ed-d564ffd54d7a.png)  

If the file is neither staged for addition nor tracked by the head commit, prints the following message instead.  
  
![image](https://user-images.githubusercontent.com/126933771/228252167-89a6417c-bc68-4827-a910-a0ff5a4a1304.png)  
  
‎   
## Find

```
java gitplit.Main find [Message]
```

Fetches all commits with the commit message __Message__ and displays their ID.  
This feature only exists in GitPlit. Does not exist in real git.  
  
![image](https://user-images.githubusercontent.com/126933771/228253984-8b3f05f0-8027-47a5-8d14-9a00e5bb3107.png)   
  
![image](https://user-images.githubusercontent.com/126933771/228254551-07dd961a-765f-4c75-8609-03dfd9b17250.png)  
  
‎   
## Checkout

```
java gitplit.Main checkout -- [FileName]
```
Takes the version of the file with __FileName__ as it exists in the head commit and puts it in the CWD.  
Overwrites if the file already exists. This new/newer version of the file is not staged for addition.  
  
![image](https://user-images.githubusercontent.com/126933771/228256246-295db822-28ac-446a-b096-b442b686f0b2.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228256376-76a317c7-26b8-4c16-86f8-10a097281aa8.png)  
‎      
‎  

```
java gitplit.Main checkout [CommitID] -- [FileName]
```
Takes the version of the file with __FileName__ as it exists in the commit with __CommitID__ and puts it in the CWD.  
__CommitID__ can be abbreviated (must be at least 6 letters).   
Overwrites if the file already exists. This new/newer version of the file is not staged for addition.  
  
![image](https://user-images.githubusercontent.com/126933771/228257986-14c7cdda-b6f4-49a7-b985-cdaa5344118c.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228258558-0db21db0-ac3b-40c7-898a-51524f409783.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228258643-761addfe-25d8-4383-b4ff-2d208adce34f.png)  
‎      
‎  

```
java gitplit.Main checkout [BranchName]
```
Takes all files of the commit pointed by the branch with __BranchName__ and puts them in the CWD.  
Overwrites if the files already exist.  
The referenced branch will now be considered the current branch.  
  
![image](https://user-images.githubusercontent.com/126933771/228260848-7d199ee5-b7fd-4529-b51c-a810ef84e8c9.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228260690-73a390b7-e341-4a04-8f38-234d38894fd9.png)   
  
![image](https://user-images.githubusercontent.com/126933771/228260963-dc16012b-35d4-4469-af2f-17c5dd5784bd.png)   
  
‎   
## Reset

```
java gitplit.Main reset [CommitID]
```
Checks out all files tracked by the commit with __CommitID__. __CommitID__ can be abbreviated (must be at least 6 letters).  
Removes currently-tracked files that are not present in the provided commit.  
The current branch will now point at the referenced commit.  
  
![image](https://user-images.githubusercontent.com/126933771/228263381-54807c1c-14d1-4af9-a4bf-fdd07624708d.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228263533-3481a3ea-f4fe-4e0a-a62b-6a41ddb5b2ee.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228263755-33e1f037-8752-48d2-899d-5e6c51942951.png)  
  
‎   
## Merge

```
java gitplit.Main merge [BranchName]
```
Merges files from the branch with __BranchName__ into the current branch.  
Couple rules here: 
* Staging areas must be empty.
* If the provided branch is an ancestor of the current branch, prints the following message instead:  
![image](https://user-images.githubusercontent.com/126933771/228266430-e862cd99-f130-45c9-8f1d-3c17c067659d.png)
* If the current branch is an ancestor of the provided branch, prints the following message instead:  
![image](https://user-images.githubusercontent.com/126933771/228267007-bc8466aa-6ec0-4d65-8ecf-05995db7dab3.png)
* If the file contents of the provided branch and the current branch are different from each ohter, it will update the file to include the contents of both.
* This is called merge conflict. Unlike real git, merge conflicts do not have to be resolved: the changes will be shown in the updated file.
  
  
![image](https://user-images.githubusercontent.com/126933771/228270258-74e46a35-13f9-444a-bb14-416ceef63bd4.png)  
![image](https://user-images.githubusercontent.com/126933771/228270527-97301361-e25f-4f09-a2ca-c658c5efd6c3.png)  
‎   
‎  
  
![image](https://user-images.githubusercontent.com/126933771/228272402-3503437b-d421-4ca2-a088-b8dc7c951c61.png)  
![image](https://user-images.githubusercontent.com/126933771/228272515-65eefefb-7e69-4bf2-aaa1-9389735b3596.png)  
    
‎   
‎  
‎   
    
![image](https://user-images.githubusercontent.com/126933771/228274511-3e7d6fb3-9660-4315-925f-69e0133922be.png)  
  
‎  
‎   
‎  
   
![image](https://user-images.githubusercontent.com/126933771/228273367-34dd90b7-92e6-4a0c-b31b-eb80deaacce4.png)  
![image](https://user-images.githubusercontent.com/126933771/228272515-65eefefb-7e69-4bf2-aaa1-9389735b3596.png)  
  
‎   
## Clone

```
java gitplit.Main clone [DirPath]
```
Copies a GitPlit repository from the CWD and puts it into __DirPath__.  
Also creates the corresponding java & class files in __DirPath__ so that the user can run GitPlit from __DirPath__ without having to copy over the program files manually.  

Overwrites if a GitPlit repository already exists in __DirPath__.  
  
![image](https://user-images.githubusercontent.com/126933771/228852037-3e44f1df-3653-49c9-9a14-974488bebe17.png)  
  
![image](https://user-images.githubusercontent.com/126933771/228852215-7e7c9829-bb20-4dca-9c8f-1a42eb9d24e9.png)  
‎      
‎  

```
java gitplit.Main clone [RepoPath] [DirPath]
```
Copies a GitPlit repository from __RepoPath__ and puts it into __DirPath__.  
Also creates the corresponding java & class files in __DirPath__ so that the user can run GitPlit from __DirPath__ without having to copy over the program files manually.  
  
Overwrites if a GitPlit repository already exists in __DirPath__.  
  
![image](https://user-images.githubusercontent.com/126933771/228853165-d052aeb4-638a-49bb-99d2-f56fe9582652.png)  
‎  
  
![image](https://user-images.githubusercontent.com/126933771/228853762-a20265c9-8429-44ac-83aa-1ce83aa00d52.png)  
‎  
  
![image](https://user-images.githubusercontent.com/126933771/228853852-723b9249-422d-4499-886f-2435c586d967.png)
  
‎   
  
## Technologies  
* Java io & nio package
   * Streams including Buffered/ByteArray OutputStream
   * File & Serializable related classes and methods
* Java security
   * Sha3-256 algorithm
