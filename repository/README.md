# repository
Simple implementation of a SeaClouds Repository.

## Usage ##

The normal usage of a REST service. 

/data contains endpoint to upload/download data.

Supported methods:

###POST /data

Create an object in the repository, identified by an id. The id may be given in the `id` header. 
If not, a random id will be autoassigned.

When retrieving the data, the content type is the one set in the POST.

The data may be uploaded in a multipart form. The content to upload must be in `data` field.


Returns:

* 201: Created; the body contains the identifier
* 409: Id exists

Examples:

`curl -H"id:3" http://localhost:8010/data -X POST -H"Content-type:text/plain" -d"this is a plain text"`

`curl -H"id:4" http://localhost:8010/data -X POST -F data='{"value": "json"};type=application/json' -H"Content-Type: multipart/form -data"`

###GET /data/{id}

Retrieves an object previously stored. The content type of the response is the one set when storing the object.

Returns:

* 200: Ok; the body contains the object
* 404: The id is not found

Examples:


`curl -v localhost:8010/data/5`

###DELETE /data/{id}

Removes an object from the repository.

Returns:

* 200: Ok.
* 404: The id was not found

Examples:

`curl -v localhost:8010/data/4 -X DELETE`

## TODO ##

Save multipart data to filesystems.
