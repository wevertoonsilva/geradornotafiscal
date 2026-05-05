package br.com.itau.geradornotafiscal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Destinatario {
	@JsonProperty("nome")
	private String nome;

	@JsonProperty("tipo_pessoa")
	@NotNull
	private TipoPessoa tipoPessoa;

	@JsonProperty("regime_tributacao")
	private RegimeTributacaoPJ regimeTributacao;

	@JsonProperty("documentos")
	private List<Documento> documentos;

	@JsonProperty("enderecos")
	@NotNull
	@NotEmpty
	private List<Endereco> enderecos;

    public boolean isPjWithRegimeNull() {
        return TipoPessoa.JURIDICA.equals(tipoPessoa) && regimeTributacao == null;
    }
}




