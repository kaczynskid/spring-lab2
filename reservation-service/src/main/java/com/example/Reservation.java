package com.example;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Reservation {

	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "reservation_seq")
	@SequenceGenerator(name = "reservation_seq", sequenceName = "reservation_seq")
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String lang;

	public Reservation(String name, String lang) {
		this(null, name, lang);
	}
}
