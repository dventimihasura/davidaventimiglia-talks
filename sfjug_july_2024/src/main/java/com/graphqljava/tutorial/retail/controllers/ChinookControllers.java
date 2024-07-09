package com.graphqljava.tutorial.retail.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.BatchMapping;
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
	@QueryMapping(name = "ArtistById") Artist
	    artistById (ArgumentValue<Integer> id) {
	    for (Artist a : jdbcClient.sql("select * from \"Artist\" where \"ArtistId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Artist") List<Artist>
	    artist (ArgumentValue<Integer> limit) {
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
			new Album(rs.getInt("AlbumId"),
				  rs.getString("Title"),
				  rs.getInt("ArtistId"));}};
	@SchemaMapping Album Album (Track track) {
	    return
		jdbcClient
		.sql("select * from \"Album\" where \"AlbumId\" = ? limit 1")
		.param(track.AlbumId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@BatchMapping(field = "Albums") public Map<Artist, List<Album>>
	    albumsForArtist (List<Artist> artists) {
	    return
		jdbcClient
		.sql("select * from \"Album\" where \"ArtistId\" in (:ids)")
		.param("ids", artists.stream().map(x -> x.ArtistId()).toList())
		.query(mapper)
		.list()
		.stream()
		.collect(Collectors.groupingBy(x -> artists.stream().collect(Collectors.groupingBy(Artist::ArtistId)).get(x.ArtistId()).getFirst()));}
	@QueryMapping(name = "AlbumById") Album
	    albumById (ArgumentValue<Integer> id) {
	    for (Album a : jdbcClient.sql("select * from \"Album\" where \"AlbumId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Album") List<Album>
	    album (ArgumentValue<Integer> limit) {
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
			new Customer(rs.getInt("CustomerId"),
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
				     rs.getInt("SupportRepId"));}};
	@BatchMapping(field = "Customers") public Map<Employee, List<Customer>>
	    customersForEmployee (List<Employee> employees) {
	    return
		jdbcClient
		.sql("select * from \"Customer\" where \"SupportRepId\" in (:ids)")
		.param("ids", employees.stream().map(x -> x.EmployeeId()).toList())
		.query(mapper)
		.list()
		.stream()
		.collect(Collectors.groupingBy(x -> employees.stream().collect(Collectors.groupingBy(Employee::EmployeeId)).get(x.SupportRepId()).getFirst()));}
	@QueryMapping(name = "CustomerById") Customer
	    customerById (ArgumentValue<Integer> id) {
	    for (Customer a : jdbcClient.sql("select * from \"Customer\" where \"CustomerId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Customer") List<Customer>
	    customer (ArgumentValue<Integer> limit) {
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
			new Employee(rs.getInt("EmployeeId"),
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
				     rs.getString("Email"));}};
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
	@QueryMapping(name = "EmployeeById") Employee
	    employeeById (ArgumentValue<Integer> id) {
	    for (Employee a : jdbcClient.sql("select * from \"Employee\" where \"EmployeeId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Employee") List<Employee>
	    employee (ArgumentValue<Integer> limit) {
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
			new Genre(rs.getInt("GenreId"),
				  rs.getString("Name"));}};
	@SchemaMapping Genre Genre (Track track) {
	    return
		jdbcClient
		.sql("select * from \"Genre\" where \"GenreId\" = ? limit 1")
		.param(track.GenreId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping(name = "GenreById") Genre
	    genreById (ArgumentValue<Integer> id) {
	    for (Genre a : jdbcClient.sql("select * from \"Genre\" where \"GenreId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
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
			new Invoice(rs.getInt("InvoiceId"),
				    rs.getInt("CustomerId"),
				    rs.getString("InvoiceDate"),
				    rs.getString("BillingAddress"),
				    rs.getString("BillingCity"),
				    rs.getString("BillingState"),
				    rs.getString("BillingCountry"),
				    rs.getString("BillingPostalCode"),
				    rs.getFloat("Total"));}};
	@SchemaMapping Invoice Invoice (InvoiceLine invoiceLine) {
	    return
		jdbcClient
		.sql("select * from \"Invoice\" where \"InvoiceId\" = ? limit 1")
		.param(invoiceLine.InvoiceId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@BatchMapping(field = "Invoices") public Map<Customer, List<Invoice>>
	    invoicesForCustomer (List<Customer> customers) {
	    return jdbcClient
		.sql("select * from \"Invoice\" where \"CustomerId\" in (:ids)")
		.param("ids", customers.stream().map(x -> x.CustomerId()).toList())
		.query(mapper)
		.list()
		.stream()
		.collect(Collectors.groupingBy(x -> customers.stream().collect(Collectors.groupingBy(Customer::CustomerId)).get(x.CustomerId()).getFirst()));}
	@QueryMapping(name = "InvoiceById") Invoice
	    invoiceById (ArgumentValue<Integer> id) {
	    for (Invoice a : jdbcClient.sql("select * from \"Invoice\" where \"InvoiceId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Invoice") List<Invoice>
	    invoice (ArgumentValue<Integer> limit) {
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
			new InvoiceLine(rs.getInt("InvoiceLineId"),
					rs.getInt("InvoiceId"),
					rs.getInt("TrackId"),
					rs.getFloat("UnitPrice"),
					rs.getInt("Quantity"));}};
	@BatchMapping(field = "InvoiceLines") public Map<Invoice, List<InvoiceLine>>
	    invoiceLinesForInvoice (List<Invoice> invoices) {
	    return jdbcClient
		.sql("select * from \"InvoiceLine\" where \"InvoiceId\" in (:ids)")
		.param("ids", invoices.stream().map(x -> x.InvoiceId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> invoices.stream().collect(Collectors.groupingBy(Invoice::InvoiceId)).get(x.InvoiceId()).getFirst()));}
	@BatchMapping(field = "InvoiceLines") public Map<Track, List<InvoiceLine>>
	    invoiceLinesForTrack (List<Track> tracks) {
	    return jdbcClient
		.sql("select * from \"InvoiceLine\" where \"TrackId\" in (:ids)")
		.param("ids", tracks.stream().map(x -> x.TrackId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> tracks.stream().collect(Collectors.groupingBy(Track::TrackId)).get(x.TrackId()).getFirst()));}
	@QueryMapping(name = "InvoiceLineById") InvoiceLine
	    invoiceLineById (ArgumentValue<Integer> id) {
	    for (InvoiceLine a : jdbcClient.sql("select * from \"InvoiceLine\" where \"InvoiceLineId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "InvoiceLine") List<InvoiceLine>
	    invoiceLine (ArgumentValue<Integer> limit) {
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
			new MediaType(rs.getInt("MediaTypeId"),
				      rs.getString("Name"));}};
	@SchemaMapping MediaType MediaType (Track track) {
	    return
		jdbcClient
		.sql("select * from \"MediaType\" where \"MediaTypeId\" = ? limit 1")
		.param(track.MediaTypeId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping(name = "MediaTypeById") MediaType
	    mediaTypeById (ArgumentValue<Integer> id) {
	    for (MediaType a : jdbcClient.sql("select * from \"MediaType\" where \"MediaTypeId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "MediaType") List<MediaType>
	    mediaType (ArgumentValue<Integer> limit) {
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
			new Playlist(rs.getInt("PlaylistId"),
				     rs.getString("Name"));}};
	@SchemaMapping Playlist Playlist (PlaylistTrack playlistTrack) {
	    return
		jdbcClient
		.sql("select * from \"Playlist\" where \"PlaylistId\" = ? limit 1")
		.param(playlistTrack.PlaylistId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@QueryMapping(name = "PlaylistById") Playlist
	    playlistById (ArgumentValue<Integer> id) {
	    for (Playlist a : jdbcClient.sql("select * from \"Playlist\" where \"PlaylistId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Playlist") List<Playlist>
	    playlist (ArgumentValue<Integer> limit) {
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
			new PlaylistTrack(rs.getInt("PlaylistId"),
					  rs.getInt("TrackId"));}};
	@BatchMapping(field = "PlaylistTracks") public Map<Playlist, List<PlaylistTrack>>
	    playlistTracksForPlaylist (List<Playlist> playlists) {
	    return jdbcClient
		.sql("select * from \"PlaylistTrack\" where \"PlaylistId\" in (:ids)")
		.param("ids", playlists.stream().map(x -> x.PlaylistId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> playlists.stream().collect(Collectors.groupingBy(Playlist::PlaylistId)).get(x.PlaylistId()).getFirst()));}
	@BatchMapping(field = "PlaylistTracks") public Map<Track, List<PlaylistTrack>>
	    playlistTracksForTrack (List<Track> tracks) {
	    return jdbcClient
		.sql("select * from \"PlaylistTrack\" where \"TrackId\" in (:ids)")
		.param("ids", tracks.stream().map(x -> x.TrackId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> tracks.stream().collect(Collectors.groupingBy(Track::TrackId)).get(x.TrackId()).getFirst()));}
	@QueryMapping(name = "PlaylistTrackById") PlaylistTrack
	    playlistTrackById (ArgumentValue<Integer> id) {
	    for (PlaylistTrack a : jdbcClient.sql("select * from \"PlaylistTrack\" where \"PlaylistId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "PlaylistTrack") List<PlaylistTrack>
	    playlistTrack (ArgumentValue<Integer> limit) {
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
			new Track(rs.getInt("TrackId"),
				  rs.getString("Name"),
				  rs.getInt("AlbumId"),
				  rs.getInt("MediaTypeId"),
				  rs.getInt("GenreId"),
				  rs.getString("Composer"),
				  rs.getInt("Milliseconds"),
				  rs.getInt("Bytes"),
				  rs.getFloat("UnitPrice"));}};
	@SchemaMapping Track Track (InvoiceLine invoiceLine) {
	    return
		jdbcClient
		.sql("select * from \"Track\" where \"TrackId\" = ? limit 1")
		.param(invoiceLine.TrackId())
		.query(mapper)
		.optional()
		.orElse(null);}
	@BatchMapping(field = "Tracks") public Map<Album, List<Track>>
	    tracksForAlbum (List<Album> albums) {
	    return jdbcClient
		.sql("select * from \"Track\" where \"AlbumId\" in (:ids)")
		.param("ids", albums.stream().map(x -> x.AlbumId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> albums.stream().collect(Collectors.groupingBy(Album::AlbumId)).get(x.AlbumId()).getFirst()));}
	@BatchMapping(field = "Tracks") public Map<Genre, List<Track>>
	    tracksForGenre (List<Genre> genres) {
	    return jdbcClient
		.sql("select * from \"Track\" where \"GenreId\" in (:ids)")
		.param("ids", genres.stream().map(x -> x.GenreId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> genres.stream().collect(Collectors.groupingBy(Genre::GenreId)).get(x.GenreId()).getFirst()));}
	@BatchMapping(field = "Tracks") public Map<MediaType, List<Track>>
	    tracksForMediaType (List<MediaType> mediaTypes) {
	    return jdbcClient
		.sql("select * from \"Track\" where \"MediaTypeId\" in (:ids)")
		.param("ids", mediaTypes.stream().map(x -> x.MediaTypeId()).toList())
		.query(mapper)
		.list().stream().collect(Collectors.groupingBy(x -> mediaTypes.stream().collect(Collectors.groupingBy(MediaType::MediaTypeId)).get(x.MediaTypeId()).getFirst()));}
	@QueryMapping(name = "TrackById") Track
	    trackById (ArgumentValue<Integer> id) {
	    for (Track a : jdbcClient.sql("select * from \"Track\" where \"TrackId\" = ?").param(id.value()).query(mapper).list()) return a;
	    return null;}
	@QueryMapping(name = "Track") List<Track>
	    track (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select * from \"Track\"") :
		jdbcClient.sql("select * from \"Track\" limit ?").param(limit.value());
	    return
		spec
		.query(mapper)
		.list();}}}
