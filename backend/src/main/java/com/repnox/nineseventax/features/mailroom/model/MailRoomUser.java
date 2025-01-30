package com.repnox.nineseventax.features.mailroom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.io.Serializable;

@Entity
@Table(name="mail_room_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRoomUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2589803512485318184L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "USERNUM_GEN")
    @TableGenerator(name = "USERNUM_GEN", table = "MAIL_ROOM_USERNUM_GEN_TBL", pkColumnName = "USERNUM_GEN_NAME", valueColumnName = "USERNUM_GEN_VAL", pkColumnValue = "USERNUM_GEN_PK", initialValue = 40000, allocationSize = 1)
    private Long userNum;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "password")
	private String password;
}
