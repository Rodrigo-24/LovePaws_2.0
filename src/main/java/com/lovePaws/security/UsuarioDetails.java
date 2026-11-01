package com.lovePaws.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.Usuario;

public class UsuarioDetails implements UserDetails {
	
	private final String username;
	private final String passwordHash;
	private final Collection<? extends GrantedAuthority> authorities;
	private final boolean activo;

	public UsuarioDetails(Usuario usuario) {
		this.username = usuario.getUsername();
		this.passwordHash = usuario.getPasswordHash();
		this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().name()));
		this.activo = usuario.getEstado() == EstadoUsuario.ACTIVO;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
