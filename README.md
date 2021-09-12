# OR_scheduling_project
- Authors: [Kyuhee Jo](kjo3@jhu.edu), [Cyrus Mintz](cmintz2@jhmi.edu)
- Automatic OR scheduling for Johns Hopkins Hospital

## The Task

Operating room schedules are manual devised every day according to long list of rule structures which specifies the ratio of residents:attendings, nurses:attendings in a given operating room. The required ratio varies depending on the specific buildings. Attendings are faculty member who can cover either one room without any staff or multiple rooms each with one staff (residents or nurses). The goal is to produce a optimized solution that (1) satisfies the given rules (2) minimizes the number of attendings and (3) satisfies as many preferred constraints as possible. The given information is the number of residents/nurses present on call as well as the number and location of operating rooms that needs staffing in a given day. 

| Method        | What it does |
| ------------- | ------------- |
| IsValid (Method)  | Checks if the given combination satisfies the hard constraints 
                      (e.g. maximum number of residents/ number of solo etc.)  |
| Content Cell  | Content Cell  |


## What is Tabu Search? 

## How to use 

README.docx contains detailed instructions to use the program to generate schedules from excel sheet. 
