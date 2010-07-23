#!/usr/bin/env ruby
#
# Pull out the list of options from fulljslint.js.
#
# @(#) $Id$

opts = {
  # Hard-code these, as they're not part of the boolean options.
  "indent" => ['The number of spaces used for indentation (default is 4)', 'Integer'],
  'maxerr' => ['The maximum number of warnings reported (default is 50)', 'Integer'],
  # NB: Slight variant of original text.
  'predef' => ['The names of predefined global variables.', 'StringArray'],
}

File.open(ARGV[0]) do |fh|
  while line = fh.gets do
    # puts ">> #{line}"
    if (line =~ /\s+boolOptions\s*=\s*\{/) ... (line =~ /\}/)
      if md = line.match(/(\w+).*\/\/ (.*)/)
        opts[ md[1] ] = [md[2].capitalize, 'Boolean']
      end
    end
  end
end

def update_file(f, indent, opts)
  contents = []
  File.open(f) do |fh|
    while line = fh.gets do
      if line =~ /\/\/\s*BEGIN-OPTIONS/
        # Skip up to the end of the options section.
        while line !~ /\/\/\s*END-OPTIONS/
          line = fh.gets
        end

        # And start rewriting.
        contents << "#{indent}// BEGIN-OPTIONS"
        opts.keys.sort.map do |k|
          # key, desc, type
          contents << yield(k, opts[k][0], opts[k][1])
        end
        contents << "#{indent}// END-OPTIONS"
      else
        contents << line.rstrip()
      end
    end
  end
  File.open(f, "w") do |fh|
    fh.write(contents.join("\n") + "\n")
  end
end

indent = "    "
update_file(ARGV[1], indent, opts) do |k,desc,type|
  descEscaped = desc.gsub(/"/, '\\"')
  ["#{indent}/** #{desc} */",
   "#{indent}#{k.upcase}(\"#{descEscaped}\", #{type}.class),",
   ""]
end

update_file(ARGV[2], indent, opts) do |k,desc,type|
  descEscaped = desc.gsub(/"/, '\\"')
  ["#{indent}@Parameter(names = \"--#{k}\", description = \"#{descEscaped}\")",
   "#{indent}public #{type} #{k} = null;",
   ""]
end
