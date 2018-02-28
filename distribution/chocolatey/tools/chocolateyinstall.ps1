$toolsDir   = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"

Get-ChocolateyUnzip "$toolsDir\\vet.zip" "$toolsDir\\vet"
rm "$toolsDir\\vet.zip"
Install-BinFile "vet" "$toolsDir\\vet\\bin\\vet.bat"
