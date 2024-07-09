package com.graphqljava.tutorial.retail.models;

public class ChinookModels {
    public static
	record Album (
		      Integer AlbumId,
		      String Title,
		      Integer ArtistId
		      ) {}

    public static
	record Artist (
		       Integer ArtistId,
		       String Name
		       ) {}

    public static
	record Customer (
			 Integer CustomerId,
			 String FirstName,
			 String LastName,
			 String Company,
			 String Address,
			 String City,
			 String State,
			 String Country,
			 String PostalCode,
			 String Phone,
			 String Fax,
			 String Email,
			 Integer SupportRepId
			 ) {}

    public static
	record Employee (
			 Integer EmployeeId,
			 String LastName,
			 String FirstName,
			 String Title,
			 Integer ReportsTo,
			 String BirthDate,
			 String HireDate,
			 String Address,
			 String City,
			 String State,
			 String Country,
			 String PostalCode,
			 String Phone,
			 String Fax,
			 String Email
			 ) {}

    public static
	record Genre (
		      Integer GenreId,
		      String Name
		      ) {}

    public static
	record Invoice (
			Integer InvoiceId,
			Integer CustomerId,
			String InvoiceDate,
			String BillingAddress,
			String BillingCity,
			String BillingState,
			String BillingCountry,
			String BillingPostalCode,
			Float Total
			) {}

    public static
	record InvoiceLine (
			    Integer InvoiceLineId,
			    Integer InvoiceId,
			    Integer TrackId,
			    Float UnitPrice,
			    Integer Quantity
			    ) {}


    public static
	record MediaType (
			  Integer MediaTypeId,
			  String Name
			  ) {}


    public static
	record Playlist (
			 Integer PlaylistId,
			 String Name
			 ) {}

    public static
	record PlaylistTrack (
			      Integer PlaylistId,
			      Integer TrackId
			      ) {}

    public static
	record Track (
		      Integer TrackId,
		      String Name,
		      Integer AlbumId,
		      Integer MediaTypeId,
		      Integer GenreId,
		      String Composer,
		      Integer Milliseconds,
		      Integer Bytes,
		      Float UnitPrice
		      ) {}
}
