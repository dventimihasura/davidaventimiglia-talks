package com.graphqljava.tutorial.retail.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Controller;

import com.graphqljava.tutorial.retail.models.ChinookModels.Album;
import com.graphqljava.tutorial.retail.models.ChinookModels.Artist;
import com.graphqljava.tutorial.retail.models.ChinookModels.Customer;
import com.graphqljava.tutorial.retail.models.ChinookModels.Employee;
import com.graphqljava.tutorial.retail.models.ChinookModels.Genre;
import com.graphqljava.tutorial.retail.models.ChinookModels.Invoice;
import com.graphqljava.tutorial.retail.models.ChinookModels.InvoiceLine;
import com.graphqljava.tutorial.retail.models.ChinookModels.MediaType;
import com.graphqljava.tutorial.retail.models.ChinookModels.Playlist;
import com.graphqljava.tutorial.retail.models.ChinookModels.PlaylistTrack;
import com.graphqljava.tutorial.retail.models.ChinookModels.Track;


public class ChinookControllers {
    @Controller public static class ArtistController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Artist>
	    mapper = new RowMapper<>() {
		    public Artist mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Artist(rs.getInt("ArtistId"),
				   rs.getString("Name"));}};
	@SchemaMapping Artist Artist (Album album) {
	    return
		jdbcClient
		.sql("select * from \"Artist\" where \"ArtistId\" = ? limit 1")
		.param(album.ArtistId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping List<Artist>
	    Artist (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Artist\"") :
		jdbcClient.sql("select * from \"Artist\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class AlbumController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Album>
	    mapper = new RowMapper<>() {
		    public Album mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Album(
				  rs.getInt("AlbumId"),
				  rs.getString("Title"),
				  rs.getInt("ArtistId")
				  );}};
	@SchemaMapping Album Album (Track track) {
	    return
		jdbcClient
		.sql("select * from \"Album\" where \"AlbumId\" = ? limit 1")
		.param(track.AlbumId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@SchemaMapping List<Album>
	    Albums (Artist artist, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Album\" where \"ArtistId\" = ?").param(artist.ArtistId()) :
		jdbcClient.sql("select * from \"Album\" where \"ArtistId\" = ? limit ?").param(artist.ArtistId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<Album>
	    Album (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Album\"") :
		jdbcClient.sql("select * from \"Album\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class CustomerController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Customer>
	    mapper = new RowMapper<>() {
		    public Customer mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Customer(
				     rs.getInt("CustomerId"),
				     rs.getString("FirstName"),
				     rs.getString("LastName"),
				     rs.getString("Company"),
				     rs.getString("Address"),
				     rs.getString("City"),
				     rs.getString("State"),
				     rs.getString("Country"),
				     rs.getString("PostalCode"),
				     rs.getString("Phone"),
				     rs.getString("Fax"),
				     rs.getString("Email"),
				     rs.getInt("SupportRepId")
				     );}};
	@SchemaMapping List<Customer>
	    Customers (Employee employee, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Customer\" where \"SupportRepId\" = ?").param(employee.EmployeeId()) :
		jdbcClient.sql("select * from \"Customer\" where \"SupportRepId\" = ? limit ?").param(employee.EmployeeId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<Customer>
	    Customer (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Customer\"") :
		jdbcClient.sql("select * from \"Customer\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class EmployeeController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Employee>
	    mapper = new RowMapper<>() {
		    public Employee mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Employee(
				     rs.getInt("EmployeeId"),
				     rs.getString("LastName"),
				     rs.getString("FirstName"),
				     rs.getString("Title"),
				     rs.getInt("ReportsTo"),
				     rs.getString("BirthDate"),
				     rs.getString("HireDate"),
				     rs.getString("Address"),
				     rs.getString("City"),
				     rs.getString("State"),
				     rs.getString("Country"),
				     rs.getString("PostalCode"),
				     rs.getString("Phone"),
				     rs.getString("Fax"),
				     rs.getString("Email")
				     );}};
	@SchemaMapping Employee Employee (Customer customer) {
	    return
		jdbcClient
		.sql("select * from \"Employee\" where \"EmployeeId\" = ? limit 1")
		.param(customer.SupportRepId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@SchemaMapping Employee Manager (Employee employee) {
	    return
		jdbcClient
		.sql("select * from \"Employee\" where \"EmployeeId\" = ? limit 1")
		.param(employee.ReportsTo())
		.query(mapper)
		.optional()
		.orElse(null);}
	@SchemaMapping List<Employee>
	    Reports (Employee employee, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Employee\" where \"ReportsTo\" = ?").param(employee.ReportsTo()) :
		jdbcClient.sql("select * from \"Employee\" where \"ReportsTo\" = ? limit ?").param(employee.ReportsTo()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<Employee>
	    Employee (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Employee\"") :
		jdbcClient.sql("select * from \"Employee\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class GenreController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Genre>
	    mapper = new RowMapper<>() {
		    public Genre mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Genre(
				  rs.getInt("GenreId"),
				  rs.getString("Name")
				  );}};
	@SchemaMapping Genre Genre (Track track) {
	    return
		jdbcClient
		.sql("select * from \"Genre\" where \"GenreId\" = ? limit 1")
		.param(track.GenreId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping List<Genre>
	    Genre (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Genre\"") :
		jdbcClient.sql("select * from \"Genre\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class InvoiceController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Invoice>
	    mapper = new RowMapper<>() {
		    public Invoice mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Invoice(
				    rs.getInt("InvoiceId"),
				    rs.getInt("CustomerId"),
				    rs.getString("InvoiceDate"),
				    rs.getString("BillingAddress"),
				    rs.getString("BillingCity"),
				    rs.getString("BillingState"),
				    rs.getString("BillingCountry"),
				    rs.getString("BillingPostalCode"),
				    rs.getFloat("Total")
				    );}};
	@SchemaMapping Invoice Invoice (InvoiceLine invoiceLine) {
	    return
		jdbcClient
		.sql("select * from \"Invoice\" where \"InvoiceId\" = ? limit 1")
		.param(invoiceLine.InvoiceId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@SchemaMapping List<Invoice>
	    Invoices (Customer customer, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Invoice\" where \"CustomerId\" = ?").param(customer.CustomerId()) :
		jdbcClient.sql("select * from \"Invoice\" where \"CustomerId\" = ? limit ?").param(customer.CustomerId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<Invoice>
	    Invoice (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Invoice\"") :
		jdbcClient.sql("select * from \"Invoice\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class InvoiceLineController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<InvoiceLine>
	    mapper = new RowMapper<>() {
		    public InvoiceLine mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new InvoiceLine(
					rs.getInt("InvoiceLineId"),
					rs.getInt("InvoiceId"),
					rs.getInt("TrackId"),
					rs.getFloat("UnitPrice"),
					rs.getInt("Quantity")
					);}};
	@SchemaMapping List<InvoiceLine>
	    InvoiceLines (Invoice invoice, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"InvoiceLine\" where \"InvoiceId\" = ?").param(invoice.InvoiceId()) :
		jdbcClient.sql("select * from \"InvoiceLine\" where \"InvoiceId\" = ? limit ?").param(invoice.InvoiceId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@SchemaMapping List<InvoiceLine>
	    InvoiceLines (Track track, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"InvoiceLine\" where \"TrackId\" = ?").param(track.TrackId()) :
		jdbcClient.sql("select * from \"InvoiceLine\" where \"TrackId\" = ? limit ?").param(track.TrackId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<InvoiceLine>
	    InvoiceLine (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"InvoiceLine\"") :
		jdbcClient.sql("select * from \"InvoiceLine\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class MediaTypeController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<MediaType>
	    mapper = new RowMapper<>() {
		    public MediaType mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new MediaType(
				      rs.getInt("MediaTypeId"),
				      rs.getString("Name")
				      );}};
	@SchemaMapping MediaType MediaType (Track track) {
	    return
		jdbcClient
		.sql("select * from \"MediaType\" where \"MediaTypeId\" = ? limit 1")
		.param(track.MediaTypeId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping List<MediaType>
	    MediaType (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"MediaType\"") :
		jdbcClient.sql("select * from \"MediaType\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class PlaylistController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Playlist>
	    mapper = new RowMapper<>() {
		    public Playlist mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Playlist(
				     rs.getInt("PlaylistId"),
				     rs.getString("Name")
				     );}};
	@SchemaMapping Playlist Playlist (PlaylistTrack playlistTrack) {
	    return
		jdbcClient
		.sql("select * from \"Playlist\" where \"PlaylistId\" = ? limit 1")
		.param(playlistTrack.PlaylistId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping List<Playlist>
	    Playlist (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Playlist\"") :
		jdbcClient.sql("select * from \"Playlist\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class PlaylistTrackController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<PlaylistTrack>
	    mapper = new RowMapper<>() {
		    public PlaylistTrack mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new PlaylistTrack(
					  rs.getInt("PlaylistId"),
					  rs.getInt("TrackId")
					  );}};
	@SchemaMapping List<PlaylistTrack>
	    PlaylistTracks (Playlist playlist, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"PlaylistTrack\" where \"PlaylistId\" = ?").param(playlist.PlaylistId()) :
		jdbcClient.sql("select * from \"PlaylistTrack\" where \"PlaylistId\" = ? limit ?").param(playlist.PlaylistId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@SchemaMapping List<PlaylistTrack>
	    PlaylistTracks (Track track, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"PlaylistTrack\" where \"TrackId\" = ?").param(track.TrackId()) :
		jdbcClient.sql("select * from \"PlaylistTrack\" where \"TrackId\" = ? limit ?").param(track.TrackId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<PlaylistTrack>
	    PlaylistTrack (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"PlaylistTrack\"") :
		jdbcClient.sql("select * from \"PlaylistTrack\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}
    @Controller public static class TrackController {
	@Autowired JdbcClient jdbcClient;
	RowMapper<Track>
	    mapper = new RowMapper<>() {
		    public Track mapRow (ResultSet rs, int rowNum) throws SQLException {
			return
			new Track(
				  rs.getInt("TrackId"),
				  rs.getString("Name"),
				  rs.getInt("AlbumId"),
				  rs.getInt("MediaTypeId"),
				  rs.getInt("GenreId"),
				  rs.getString("Composer"),
				  rs.getInt("Milliseconds"),
				  rs.getInt("Bytes"),
				  rs.getFloat("UnitPrice")
				  );}};
	@SchemaMapping Track Track (InvoiceLine invoiceLine) {
	    return
		jdbcClient
		.sql("select * from \"Track\" where \"TrackId\" = ? limit 1")
		.param(invoiceLine.TrackId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@SchemaMapping List<Track>
	    Tracks (Album album, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Track\" where \"AlbumId\" = ?").param(album.AlbumId()) :
		jdbcClient.sql("select * from \"Track\" where \"AlbumId\" = ? limit ?").param(album.AlbumId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@SchemaMapping List<Track>
	    Tracks (Genre genre, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Track\" where \"GenreId\" = ?").param(genre.GenreId()) :
		jdbcClient.sql("select * from \"Track\" where \"GenreId\" = ? limit ?").param(genre.GenreId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@SchemaMapping List<Track>
	    Tracks (MediaType mediaType, ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Track\" where \"MediaTypeId\" = ?").param(mediaType.MediaTypeId()) :
		jdbcClient.sql("select * from \"Track\" where \"MediaTypeId\" = ? limit ?").param(mediaType.MediaTypeId()).param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}
	@QueryMapping List<Track>
	    Track (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Track\"") :
		jdbcClient.sql("select * from \"Track\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}}
